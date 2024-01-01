package com.github.vinayakmp007.microservicelogger.log;

import com.github.vinayakmp007.microservicelogger.daemon.MicroServiceLoggerDaemon;
import com.github.vinayakmp007.microservicelogger.log.concurrency.MicroServiceLogger;
import com.github.vinayakmp007.microservicelogger.log.messages.MicroServiceLogRecord;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MicroServiceLoggerDefaultLogHandler extends Handler {
    @Override
    public void

            publish(LogRecord record) {
        MicroServiceLogRecord msRecord = new MicroServiceLogRecord(record);
        MicroServiceLogger.LogDelayed(msRecord);
    }

    @Override
    public void flush() {
        if (MicroServiceLoggerDaemon.isRunning()) {
            MicroServiceLoggerDaemon.flush();
        }
    }

    @Override
    public void close() throws SecurityException {

    }
}
