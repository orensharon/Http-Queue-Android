package com.orensharon.brainq.data;

import com.orensharon.brainq.Request;
import com.orensharon.brainq.service.RetryComparator;

import java.util.concurrent.PriorityBlockingQueue;

public class RequestRepository {
    private final PriorityBlockingQueue<Request> queue;

    public RequestRepository() {
        this.queue = new PriorityBlockingQueue<>(100, new RetryComparator());
    }

    public void add(Request request) {
        this.queue.add(request);
    }

    public Request take() throws InterruptedException {
        return this.queue.take();
    }

    public void delete(Request request) {
        this.queue.remove(request);
    }
}
