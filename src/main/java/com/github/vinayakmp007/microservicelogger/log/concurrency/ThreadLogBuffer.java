package com.github.vinayakmp007.microservicelogger.log.concurrency;

import com.github.vinayakmp007.microservicelogger.log.messages.MessageRecord;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;

public class ThreadLogBuffer {

    private final long ThreadId;
    private final ConcurrentLinkedDeque<MessageRecord> queue = new ConcurrentLinkedDeque<>();

    private final AtomicLong count = new AtomicLong();

    private ThreadLogBuffer(long threadId) {
        ThreadId = threadId;
    }

    public void writeRecord(MessageRecord record) {
        queue.add(record);
        count.incrementAndGet();
    }

    public MessageRecord readRecord() {
        MessageRecord msg = queue.poll();
        if (msg != null) {
            count.decrementAndGet();
        }
        return msg;
    }

    public long getRecordCount() {
        return count.get();
    }

    public long getThreadId() {
        return ThreadId;
    }

    public static ThreadLogBuffer createThreadLogBuffer(long threadId) {
        return new ThreadLogBuffer(threadId);
    }
}
