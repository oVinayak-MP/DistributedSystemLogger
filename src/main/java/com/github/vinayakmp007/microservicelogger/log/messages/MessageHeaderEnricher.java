package com.github.vinayakmp007.microservicelogger.log.messages;

public interface MessageHeaderEnricher {

    MessageHeader enrichMessageHeader(MessageHeader messageHeader);
}
