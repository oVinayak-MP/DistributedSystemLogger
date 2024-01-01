package com.github.vinayakmp007.microservicelogger.daemon;

import com.github.vinayakmp007.microservicelogger.configuration.ConfigConstants;
import com.github.vinayakmp007.microservicelogger.configuration.MicroServiceLoggerConfigReader;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MicroServiceLoggerDaemonTest {

    @Mock
    private MicroServiceLoggerConfigReader configReader;

    @Mock
    private KafkaProducer<String, MessageRecord> kafkaProducer;

    @Captor
    private ArgumentCaptor<ProducerRecord<String, MessageRecord>> producerRecordCaptor;

    private MicroServiceLoggerDaemon daemon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(configReader.getStringProperty(ConfigConstants.TOPIC_NAME)).thenReturn("test-topic");
        when(configReader.getStringProperty(ConfigConstants.DAEMON_SLEEP_TIME_IN_MS, "20")).thenReturn("100");
        when(configReader.getStringProperty(ConfigConstants.SLICE_SIZE, "50000")).thenReturn("100");
        daemon = new MicroServiceLoggerDaemon(configReader, kafkaProducer);
    }

    @AfterEach
    void tearDown() {
        daemon.interrupt();
    }

    @Test
    void testStartDaemonWhenAlreadyStartedThenThrowsIllegalStateException() {
        MicroServiceLoggerDaemon.startDaemon(configReader, kafkaProducer);
        assertThrows(IllegalStateException.class,
                () -> MicroServiceLoggerDaemon.startDaemon(configReader, kafkaProducer));
    }

    @Test
    void testFlushWhenDaemonNotStartedThenThrowsIllegalStateException() {
        daemon.interrupt(); // Stop the daemon to simulate not started state
        assertThrows(IllegalStateException.class, MicroServiceLoggerDaemon::flush);
    }

    @Test
    void testIsRunningWhenDaemonStartedThenReturnsTrue() {
        assertTrue(MicroServiceLoggerDaemon.isRunning(), "Daemon should be running");
    }

}