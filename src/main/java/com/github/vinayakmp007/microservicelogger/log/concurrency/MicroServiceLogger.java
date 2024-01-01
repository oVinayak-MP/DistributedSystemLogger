package com.github.vinayakmp007.microservicelogger.log.concurrency;

import com.github.vinayakmp007.microservicelogger.configuration.ConfigConstants;
import com.github.vinayakmp007.microservicelogger.configuration.MicroServiceLoggerConfigReader;
import com.github.vinayakmp007.microservicelogger.configuration.SystemPropertyReader;
import com.github.vinayakmp007.microservicelogger.log.id.IdGenerator;
import com.github.vinayakmp007.microservicelogger.log.id.RandomStringIdGenerator;
import com.github.vinayakmp007.microservicelogger.log.messages.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MicroServiceLogger {

    private static final MicroServiceLoggerConfigReader microServiceLoggerConfigReader = new SystemPropertyReader();
    private static final Logger logger = Logger.getLogger(MicroServiceLogger.class.getName());

    private static final List<MessageHeaderEnricher> messageHeaderEnrichers = List
            .of(new DefaultMessageHeaderEnricher());
    private static IdGenerator idGenerator = new RandomStringIdGenerator();
    private static final ThreadLocal<ThreadLoggerState> threadLoggerStateThreadLocal = new ThreadLocal<>();

    private static final AtomicBoolean maximumRecordsReached = new AtomicBoolean(false);

    public static void LogDelayed(MicroServiceLogRecord logEntry) {
        registerNewThreadLoggerForCurrentThreadIfNotRegistered();
        if (shouldAddRecordAfterMemoryCheck()) {
            MessageHeader messageHeader = createMessageHeader();
            enrichMessageHeader(messageHeader);
            MessageRecord messageRecord = new MessageRecord(messageHeader, logEntry);
            logRecord(messageRecord);
        }
    }

    public static String createNewLoggingSession() {
        return createNewLoggingSession(idGenerator.getNewId());
    }

    public static void startSpan(String spanName) {
        ThreadLoggerState.Span span = new ThreadLoggerState.Span(spanName, idGenerator.getNewId());
        threadLoggerStateThreadLocal.get().addSpan(span);
    }

    public static void endSpan() {
        threadLoggerStateThreadLocal.get().removeCurrentSpan();
    }

    public static String createNewLoggingSession(String correlationId) {
        registerNewThreadLoggerForCurrentThreadIfNotRegistered();
        endCurrentLoggingSession();
        threadLoggerStateThreadLocal.get().setCorrelationID(correlationId);
        threadLoggerStateThreadLocal.get().startSession();
        return getCurrentCorrelationId();
    }

    public static String endCurrentLoggingSession() {
        registerNewThreadLoggerForCurrentThreadIfNotRegistered();
        threadLoggerStateThreadLocal.get().setCorrelationID(idGenerator.getNewId());
        threadLoggerStateThreadLocal.get().emptyAllStack();
        threadLoggerStateThreadLocal.get().endSession();
        return getCurrentCorrelationId();
    }

    private static boolean isRegistered() {
        return threadLoggerStateThreadLocal.get() != null;
    }

    private static void registerNewThreadLoggerForCurrentThreadIfNotRegistered() {
        if (!isRegistered()) {
            registerNewThreadLoggerForCurrentThread();
        }
    }

    public static IdGenerator getIdGenerator() {
        return idGenerator;
    }

    public static void setIdGenerator(IdGenerator idGenerator) {
        MicroServiceLogger.idGenerator = idGenerator;
    }

    public static ThreadLocal<ThreadLoggerState> getThreadLoggerStateThreadLocal() {
        registerNewThreadLoggerForCurrentThreadIfNotRegistered();
        return threadLoggerStateThreadLocal;
    }

    public static String getCurrentCorrelationId() {
        return getThreadLoggerStateThreadLocal().get().getCorrelationID();
    }

    private static void logRecord(MessageRecord messageRecord) {
        getThreadLoggerStateThreadLocal().get().addRecord(messageRecord);
    }

    private static MessageHeader createMessageHeader() {
        String parentSpanID = getThreadLoggerStateThreadLocal().get().getPreviousSpan().isPresent()
                ? getThreadLoggerStateThreadLocal().get().getPreviousSpan().get().getId() : null;
        String currentSpanName = getThreadLoggerStateThreadLocal().get().getCurrentSpan().isPresent()
                ? getThreadLoggerStateThreadLocal().get().getCurrentSpan().get().getSpanName() : null;
        String currentSpanID = getThreadLoggerStateThreadLocal().get().getCurrentSpan().isPresent()
                ? getThreadLoggerStateThreadLocal().get().getCurrentSpan().get().getId() : null;

        return new MessageHeader.MessageHeaderBuilder().setCorrelationID(MicroServiceLogger.getCurrentCorrelationId())
                .setMachineName(microServiceLoggerConfigReader.getStringProperty(ConfigConstants.MACHINE_NAME))
                .setServiceName(microServiceLoggerConfigReader.getStringProperty(ConfigConstants.SERVICE_NAME))
                .setMachinePoolName(microServiceLoggerConfigReader.getStringProperty(ConfigConstants.MACHINE_POOL_NAME))
                .setParentSpanID(parentSpanID).setSpanID(currentSpanID).setSpanName(currentSpanName)
                .setTimeStampMillis(System.currentTimeMillis()).build();
    }

    private static MessageHeader enrichMessageHeader(final MessageHeader messageHeader) {
        messageHeaderEnrichers.stream().forEach(x -> x.enrichMessageHeader(messageHeader));
        return messageHeader;
    }

    private static void registerNewThreadLoggerForCurrentThread() {
        LoggerThreadRegistry.deRegisterCurrentThread();
        ThreadLoggerState threadLoggerState = ThreadLoggerState
                .createThreadLoggerState(LoggerThreadRegistry.registerCurrentThread(), idGenerator.getNewId());
        threadLoggerStateThreadLocal.set(threadLoggerState);
    }

    private static long getPendingRecordCount() {
        return getThreadLoggerStateThreadLocal().get().getPendingRecordCount();
    }

    private static boolean shouldAddRecordAfterMemoryCheck() {
        if (Boolean.parseBoolean(microServiceLoggerConfigReader
                .getStringProperty(ConfigConstants.ENABLE_MEMORY_PROTECTION, Boolean.TRUE.toString()))) {
            long recordsPending = getPendingRecordCount();
            long max = Long.parseLong(microServiceLoggerConfigReader
                    .getStringProperty(ConfigConstants.MEMORY_PROTECTION_MAXIMUM_THRESHOLD, "5000"));
            if (recordsPending > max) {

                if (!maximumRecordsReached.get()) {
                    maximumRecordsReached.set(true); // To avoid stack overflow
                    if (Boolean.parseBoolean(microServiceLoggerConfigReader
                            .getStringProperty(ConfigConstants.DEBUG_MODE, Boolean.FALSE.toString()))) {
                        System.err.println(
                                "Error: memory_protection_maximum_threshold exceeded.Not logging in MicroServiceLogger .");
                    }
                    logger.log(Level.SEVERE,
                            "Error: memory_protection_maximum_threshold exceeded.Not logging in MicroServiceLogger pending: {0}  max : {1} .",
                            new Object[] { recordsPending, max });
                }

                return false;
            } else if (recordsPending > Long.parseLong(microServiceLoggerConfigReader
                    .getStringProperty(ConfigConstants.MEMORY_PROTECTION_WARNING_THRESHOLD, "8000"))) {
                if (!maximumRecordsReached.get()) {
                    if (Boolean.parseBoolean(microServiceLoggerConfigReader
                            .getStringProperty(ConfigConstants.DEBUG_MODE, Boolean.FALSE.toString()))) {
                        System.out.println("Warning: memory_protection_warning_threshold exceeded");
                    }
                }
                return true;
            } else if (maximumRecordsReached.get()) {
                maximumRecordsReached.set(false);
                logger.log(Level.INFO, "MicroServiceLogger back to  enabled state . ",
                        new Object[] { recordsPending, max });
            }
        }

        return true;
    }
}
