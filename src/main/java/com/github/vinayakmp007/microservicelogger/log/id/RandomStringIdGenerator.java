package com.github.vinayakmp007.microservicelogger.log.id;

import java.util.UUID;

public class RandomStringIdGenerator implements IdGenerator {
    @Override
    public String getNewId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 10).toLowerCase();
    }
}
