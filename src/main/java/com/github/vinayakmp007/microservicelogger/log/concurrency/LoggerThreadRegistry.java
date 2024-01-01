package com.github.vinayakmp007.microservicelogger.log.concurrency;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoggerThreadRegistry {

    static final List<ThreadLogBuffer> registeredThreads = new CopyOnWriteArrayList<>();

    public static ThreadLogBuffer registerCurrentThread() {
        long threadID = Thread.currentThread().threadId();
        ThreadLogBuffer threadLogBuffer;
        Optional<ThreadLogBuffer> threadLogBufferOptional = registeredThreads.stream()
                .filter(x -> x.getThreadId() == threadID).findAny();

        if (threadLogBufferOptional.isPresent()) {

            return threadLogBufferOptional.get();
        } else {
            threadLogBuffer = ThreadLogBuffer.createThreadLogBuffer(threadID);
            registeredThreads.add(threadLogBuffer);
        }

        return threadLogBuffer;
    }

    public static ThreadLogBuffer deRegisterCurrentThread() {
        long threadID = Thread.currentThread().threadId();
        ThreadLogBuffer threadLogBuffer;
        Optional<ThreadLogBuffer> threadLogBufferOptional = registeredThreads.stream()
                .filter(x -> x.getThreadId() == threadID).findAny();

        if (threadLogBufferOptional.isPresent()) {

            threadLogBuffer = threadLogBufferOptional.get();
            registeredThreads.remove(threadLogBuffer);
        } else {
            threadLogBuffer = null;

        }

        return threadLogBuffer;
    }

    public static List<ThreadLogBuffer> getRegisteredThreads() {
        return registeredThreads;
    }
}
