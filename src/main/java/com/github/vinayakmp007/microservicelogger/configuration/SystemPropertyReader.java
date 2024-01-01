package com.github.vinayakmp007.microservicelogger.configuration;

public class SystemPropertyReader implements MicroServiceLoggerConfigReader {
    @Override
    public String getStringProperty(String propertyName) {
        return System.getProperty(propertyName);
    }

    @Override
    public String getStringProperty(String propertyName, String defaultValue) {
        String val = getStringProperty(propertyName);
        return val != null ? val : defaultValue;
    }
}
