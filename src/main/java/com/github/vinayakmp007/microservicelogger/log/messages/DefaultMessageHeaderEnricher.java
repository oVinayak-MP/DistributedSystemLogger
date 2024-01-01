package com.github.vinayakmp007.microservicelogger.log.messages;

public class DefaultMessageHeaderEnricher implements MessageHeaderEnricher {

    @Override
    public MessageHeader enrichMessageHeader(MessageHeader messageHeader) {
        return messageHeader;
    }
}
