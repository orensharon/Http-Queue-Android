package com.orensharon.httpqueue;

import com.orensharon.httpqueue.service.QueueWorker;

import java.util.concurrent.ExecutorService;

public class QueueWorkerStub extends QueueWorker {
    public QueueWorkerStub(ExecutorService executor, ISystemClock clock) {
        super(executor, clock);
    }

    @Override
    public void listen() {

    }

    @Override
    public void enqueue(long requestId, long scheduledTs, Listener listener) {
        listener.run(requestId);
    }
}
