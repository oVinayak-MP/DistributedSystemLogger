package com.github.vinayakmp007.microservicelogger.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;
import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import java.util.Map;

public class MessageRecordSerializer implements Serializer<MessageRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, MessageRecord messageRecord) {
        try {
            if (messageRecord == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(messageRecord);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing MessageDto to byte[]", e);
        }
    }

    @Override
    public byte[] serialize(String topic, Headers headers, MessageRecord data) {
        return Serializer.super.serialize(topic, headers, data);
    }

    @Override
    public void close() {
        Serializer.super.close();
    }

    public MessageRecordSerializer() {
        objectMapper.findAndRegisterModules();
    }
}
