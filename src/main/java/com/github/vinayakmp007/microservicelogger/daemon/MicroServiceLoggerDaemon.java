package com.github.vinayakmp007.microservicelogger.daemon;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.SerializationException;
import com.github.vinayakmp007.microservicelogger.configuration.ConfigConstants;
import com.github.vinayakmp007.microservicelogger.configuration.MicroServiceLoggerConfigReader;
import com.github.vinayakmp007.microservicelogger.log.concurrency.LoggerThreadRegistry;
import com.github.vinayakmp007.microservicelogger.log.concurrency.ThreadLogBuffer;
import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MicroServiceLoggerDaemon extends Thread {

    static Logger logger = Logger.getLogger(MicroServiceLoggerDaemon.class.getName());

    private static final Semaphore flushSemaphore = new Semaphore(0);

    private static MicroServiceLoggerDaemon microServiceLoggerDaemon = null;

    private MicroServiceLoggerConfigReader microServiceLoggerConfigReader;

    private String topicName;

    private boolean useThreadSlicing = true;

    private long sliceSize;

    private KafkaProducer<String, MessageRecord> kafkaProducer;

    private long waitTimeinMs;

    MicroServiceLoggerDaemon(MicroServiceLoggerConfigReader microServiceLoggerConfigReader,
            KafkaProducer<String, MessageRecord> kafkaProducer) {
        this.microServiceLoggerConfigReader = microServiceLoggerConfigReader;
        this.kafkaProducer = kafkaProducer;
        setName("MicroServiceLoggerDaemon");

        waitTimeinMs = Long.parseLong(
                this.microServiceLoggerConfigReader.getStringProperty(ConfigConstants.DAEMON_SLEEP_TIME_IN_MS, "20"));

        topicName = this.microServiceLoggerConfigReader.getStringProperty(ConfigConstants.TOPIC_NAME);
        if (topicName == null) {
            throw new IllegalArgumentException("Topic name is null");
        }
        sliceSize = Long
                .parseLong(this.microServiceLoggerConfigReader.getStringProperty(ConfigConstants.SLICE_SIZE, "50000"));
        setDaemon(true);
    }

    public void run() {

        try {
            while (true) {
                List<ThreadLogBuffer> threadLogBufferList = LoggerThreadRegistry.getRegisteredThreads();

                Iterator<ThreadLogBuffer> iterator = threadLogBufferList.iterator();
                while (iterator.hasNext()) {
                    publishForAThread(iterator.next());
                }
                delay();

            }
        } catch (Throwable throwable) {
            System.out.println("Damon failed  with " + throwable);
            throw throwable;
        }

    }

    protected void delay() {
        try {
            flushSemaphore.tryAcquire(waitTimeinMs, TimeUnit.MILLISECONDS);
            flushSemaphore.drainPermits();
        } catch (InterruptedException e) {
            logger.log(Level.INFO, "MicroServiceLogger was interrupted");
        }

    }

    protected void publishForAThread(ThreadLogBuffer threadLogBuffer) {

        boolean shouldContinue = true;

        int current = 0;
        while (shouldContinue) {
            MessageRecord message = threadLogBuffer.readRecord();
            if (message != null) {
                ProducerRecord<String, MessageRecord> producerRecord = new ProducerRecord<>(topicName,
                        message.messageHeader().getKey(), message);
                try {
                    kafkaProducer.send(producerRecord);
                } catch (SerializationException exp) {
                    logger.log(Level.SEVERE, "MicroServiceLogger: SerializationException for message: %s",
                            message.microServiceLogRecord().getMessage());
                }
            } else {
                break;
            }
            current++;
            if (useThreadSlicing && current >= sliceSize) {
                flushSemaphore.release();
                break;
            }
        }
        kafkaProducer.flush();
    }

    public static void startDaemon(MicroServiceLoggerConfigReader microServiceLoggerConfigReader,
            KafkaProducer<String, MessageRecord> kafkaProducer) {
        if (microServiceLoggerDaemon != null) {
            throw new IllegalStateException("Daemon is already started");
        }
        microServiceLoggerDaemon = new MicroServiceLoggerDaemon(microServiceLoggerConfigReader, kafkaProducer);
        microServiceLoggerDaemon.start();
    }

    public static void flush() {
        if (microServiceLoggerDaemon == null) {
            throw new IllegalStateException("Daemon is not started");
        }
        flushSemaphore.release();
    }

    public static boolean isRunning() {
        return microServiceLoggerDaemon != null && microServiceLoggerDaemon.isAlive();
    }
}
