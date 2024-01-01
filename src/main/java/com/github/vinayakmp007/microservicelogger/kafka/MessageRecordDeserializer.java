package com.github.vinayakmp007.microservicelogger.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;
import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import java.util.Map;

public class MessageRecordDeserializer implements Deserializer<MessageRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public MessageRecord deserialize(String s, byte[] messageRecord) {

        try {
            if (messageRecord == null) {
                System.out.println("Null received at deserializing");
                return null;
            }
            System.out.println("Deserializing...");
            return objectMapper.readValue(new String(messageRecord, "UTF-8"), MessageRecord.class);
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to MessageDto", e);
        }
    }

    @Override
    public MessageRecord deserialize(String topic, Headers headers, byte[] data) {
        return Deserializer.super.deserialize(topic, headers, data);
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }

    public MessageRecordDeserializer() {
        objectMapper.findAndRegisterModules();
    }
}
