package com.orensharon.brainq.data;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class RequestRepository {
    private final PriorityBlockingQueue<Request> queue;

    public RequestRepository() {
        this.queue = new PriorityBlockingQueue<>(100, new RetryComparator());
    }

    public void add(Request request) {
        this.queue.add(new Request(request));
    }

    public Request take() throws InterruptedException {
        return this.queue.take();
    }

    public static class RetryComparator implements Comparator<Request> {
        @Override
        public int compare(Request x, Request y) {
            return Integer.compare(x.getRetries(), y.getRetries());
        }
    }
}
