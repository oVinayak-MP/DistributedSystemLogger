package com.github.vinayakmp007.microservicelogger.log.messages;

public class MessageHeader implements java.io.Serializable {

    final private String machineName;
    final private String machinePoolName;
    final private String serviceName;
    final private String correlationID;
    final private String parentID;
    final private long timeStampMillis;

    final private String spanID;

    final private String parentSpanID;

    final private String spanName;

    private MessageHeader(MessageHeaderBuilder builder) {
        this.correlationID = builder.correlationID;
        this.machinePoolName = builder.machinePoolName;
        this.timeStampMillis = builder.timeStampMillis;
        this.parentID = builder.parentID;
        this.serviceName = builder.serviceName;
        this.machineName = builder.machineName;
        this.spanID = builder.spanID;
        this.parentSpanID = builder.parentSpanID;
        this.spanName = builder.spanName;

    }

    public static class MessageHeaderBuilder {
        private String machineName;
        private String machinePoolName;
        private String serviceName;
        private String correlationID;
        private String parentID;

        private String spanID;

        private String parentSpanID;

        private String spanName;
        private long timeStampMillis;

        public MessageHeaderBuilder setMachineName(String machineName) {
            this.machineName = machineName;
            return this;
        }

        public MessageHeaderBuilder setMachinePoolName(String machinePoolName) {
            this.machinePoolName = machinePoolName;
            return this;
        }

        public MessageHeaderBuilder setServiceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public MessageHeaderBuilder setCorrelationID(String correlationID) {
            this.correlationID = correlationID;
            return this;
        }

        public MessageHeaderBuilder setParentID(String parentID) {
            this.parentID = parentID;
            return this;
        }

        public MessageHeaderBuilder setTimeStampMillis(long timeStampMillis) {
            this.timeStampMillis = timeStampMillis;
            return this;
        }

        public MessageHeaderBuilder setSpanID(String spanID) {
            this.spanID = spanID;
            return this;
        }

        public MessageHeaderBuilder setParentSpanID(String parentSpanID) {
            this.parentSpanID = parentSpanID;
            return this;
        }

        public MessageHeaderBuilder setSpanName(String spanName) {
            this.spanName = spanName;
            return this;
        }

        public MessageHeader build() {
            return new MessageHeader(this);
        }

    }

    public String getMachineName() {
        return machineName;
    }

    public String getMachinePoolName() {
        return machinePoolName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public String getParentID() {
        return parentID;
    }

    public long getTimeStampMillis() {
        return timeStampMillis;
    }

    public String getSpanID() {
        return spanID;
    }

    public String getParentSpanID() {
        return parentSpanID;
    }

    public String getSpanName() {
        return spanName;
    }

    public String getKey() {
        return new StringBuilder().append(machineName).append(correlationID).toString();
    }
}
