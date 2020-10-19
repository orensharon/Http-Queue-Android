package com.orensharon.httpqueue;

import com.orensharon.httpqueue.service.QueueWorker;

public class QueueWorkerStub extends QueueWorker {
    @Override
    public void listen() {

    }

    @Override
    public void enqueue(long requestId, long scheduledTs, Runnable runnable) {
        runnable.run();
    }
}
