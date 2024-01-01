package com.github.vinayakmp007.microservicelogger.configuration;

public interface MicroServiceLoggerConfigReader {

    String getStringProperty(String propertyName);

    default String getStringProperty(String propertyName, String defaultValue) {
        String val = getStringProperty(propertyName);
        return val != null ? val : defaultValue;
    }
}
