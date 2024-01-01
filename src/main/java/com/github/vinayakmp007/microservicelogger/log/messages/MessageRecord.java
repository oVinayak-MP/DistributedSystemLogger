package com.github.vinayakmp007.microservicelogger.log.messages;

public record MessageRecord(MessageHeader messageHeader, MicroServiceLogRecord microServiceLogRecord)
        implements java.io.Serializable {
}
