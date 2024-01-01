package com.github.vinayakmp007.microservicelogger.log.messages;

import java.time.Clock;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class MicroServiceLogRecord extends LogRecord {
    /**
     * Construct a LogRecord with the given level and message values.
     * <p>
     * The sequence property will be initialized with a new unique value. These sequence values are allocated in
     * increasing order within a VM.
     * <p>
     * Since JDK 9, the event time is represented by an {@link Instant}. The instant property will be initialized to the
     * {@linkplain Instant#now() current instant}, using the best available {@linkplain Clock#systemUTC() clock} on the
     * system.
     * <p>
     * The thread ID property will be initialized with a unique ID for the current thread.
     * <p>
     * All other properties will be initialized to "null".
     *
     * @param level
     *            a logging level value
     * @param msg
     *            the raw non-localized logging message (may be null)
     *
     * @see Clock#systemUTC()
     */

    final private LogRecord logRecord;

    /**
     * Construct a LogRecord with the given level and message values.
     * <p>
     * The sequence property will be initialized with a new unique value. These sequence values are allocated in
     * increasing order within a VM.
     * <p>
     * Since JDK 9, the event time is represented by an {@link Instant}. The instant property will be initialized to the
     * {@linkplain Instant#now() current instant}, using the best available {@linkplain Clock#systemUTC() clock} on the
     * system.
     * <p>
     * The thread ID property will be initialized with a unique ID for the current thread.
     * <p>
     * All other properties will be initialized to "null".
     *
     * @param level
     *            a logging level value
     * @param msg
     *            the raw non-localized logging message (may be null)
     *
     * @see Clock#systemUTC()
     */

    public MicroServiceLogRecord(LogRecord logRecord) {
        super(logRecord.getLevel(), logRecord.getMessage());
        this.logRecord = logRecord;
    }

    @Override
    public String getLoggerName() {
        return logRecord.getLoggerName();
    }

    @Override
    public void setLoggerName(String name) {
        logRecord.setLoggerName(name);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return logRecord.getResourceBundle();
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        logRecord.setResourceBundle(bundle);
    }

    @Override
    public String getResourceBundleName() {
        return logRecord.getResourceBundleName();
    }

    @Override
    public void setResourceBundleName(String name) {
        logRecord.setResourceBundleName(name);
    }

    @Override
    public Level getLevel() {
        return logRecord.getLevel();
    }

    @Override
    public void setLevel(Level level) {
        logRecord.setLevel(level);
    }

    @Override
    public long getSequenceNumber() {
        return logRecord.getSequenceNumber();
    }

    @Override
    public void setSequenceNumber(long seq) {
        logRecord.setSequenceNumber(seq);
    }

    @Override
    public String getSourceClassName() {
        return logRecord.getSourceClassName();
    }

    @Override
    public void setSourceClassName(String sourceClassName) {
        logRecord.setSourceClassName(sourceClassName);
    }

    @Override
    public String getSourceMethodName() {
        return logRecord.getSourceMethodName();
    }

    @Override
    public void setSourceMethodName(String sourceMethodName) {
        logRecord.setSourceMethodName(sourceMethodName);
    }

    @Override
    public String getMessage() {
        return logRecord.getMessage();
    }

    @Override
    public void setMessage(String message) {
        logRecord.setMessage(message);
    }

    @Override
    public Object[] getParameters() {
        return logRecord.getParameters();
    }

    @Override
    public void setParameters(Object[] parameters) {
        logRecord.setParameters(parameters);
    }

    @Override
    @Deprecated(since = "16")
    public int getThreadID() {
        return logRecord.getThreadID();
    }

    @Override
    @Deprecated(since = "16")
    public void setThreadID(int threadID) {
        logRecord.setThreadID(threadID);
    }

    @Override
    public long getLongThreadID() {
        return logRecord.getLongThreadID();
    }

    @Override
    public LogRecord setLongThreadID(long longThreadID) {
        return logRecord.setLongThreadID(longThreadID);
    }

    @Override
    public long getMillis() {
        return logRecord.getMillis();
    }

    @Override
    @Deprecated
    public void setMillis(long millis) {
        logRecord.setMillis(millis);
    }

    @Override
    public Instant getInstant() {
        return logRecord.getInstant();
    }

    @Override
    public void setInstant(Instant instant) {
        logRecord.setInstant(instant);
    }

    @Override
    public Throwable getThrown() {
        return logRecord.getThrown();
    }

    @Override
    public void setThrown(Throwable thrown) {
        logRecord.setThrown(thrown);
    }
}
