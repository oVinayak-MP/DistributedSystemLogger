package com.github.vinayakmp007.microservicelogger.log.concurrency;

import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import java.util.Optional;
import java.util.Stack;

class ThreadLoggerState {
    private String correlationID;
    private final ThreadLogBuffer threadLogBuffer;
    private State state;

    private Stack<Span> spanStack = new Stack<>();

    private ThreadLoggerState(String correlationID, ThreadLogBuffer threadLogBuffer) {
        this.correlationID = correlationID;
        this.threadLogBuffer = threadLogBuffer;
        state = State.START;
    }

    public void startSession() {
        state = State.LOGGING_SESSION;
    }

    public void endSession() {
        state = State.LOGGING_NO_SESSSION;
    }

    public void emptyAllStack() {
        spanStack.empty();
    }

    public void addSpan(Span span) {
        spanStack.push(span);
    }

    public Optional<Span> getCurrentSpan() {
        if (spanStack.empty()) {
            return Optional.empty();
        }
        return Optional.of(spanStack.peek());
    }

    public Optional<Span> getPreviousSpan() {
        if (spanStack.size() < 2) {
            return Optional.empty();
        }
        return Optional.of(spanStack.get(spanStack.size() - 2));
    }

    public Optional<Span> removeCurrentSpan() {
        if (spanStack.empty()) {
            return Optional.empty();
        }
        return Optional.of(spanStack.pop());
    }

    public String getCorrelationID() {
        return correlationID;
    }

    public void setCorrelationID(String correlationID) {
        this.correlationID = correlationID;
    }

    public void addRecord(MessageRecord messageRecord) {
        threadLogBuffer.writeRecord(messageRecord);
    }

    public static ThreadLoggerState createThreadLoggerState(ThreadLogBuffer threadLogBuffer, String correlationID) {

        return new ThreadLoggerState(correlationID, threadLogBuffer);
    }

    public long getPendingRecordCount() {
        return threadLogBuffer.getRecordCount();
    }

    public enum State {
        START, LOGGING_SESSION, LOGGING_NO_SESSSION
    }

    public static class Span {
        private final String spanName;
        private final String id;

        public Span(String spanName, String id) {
            this.spanName = spanName;
            this.id = id;
        }

        public String getSpanName() {
            return spanName;
        }

        public String getId() {
            return id;
        }
    }
}
