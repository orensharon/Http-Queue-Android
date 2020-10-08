package com.orensharon.httpqueue.service;

import android.os.SystemClock;
import android.util.Log;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class QueueWorker {

    private final static String TAG = QueueWorker.class.getSimpleName();

    private volatile boolean started;

    private ExecutorService executor;
    private final PriorityBlockingQueue<QueuedRequest> requests;

    public QueueWorker() {
        this.requests = new PriorityBlockingQueue<>(10, new QueueComparator());
    }


    public void enqueue(long requestId, long scheduledTs, Runnable runnable) {
        this.requests.add(new QueuedRequest(requestId, scheduledTs, runnable));
    }

    public void listen() {
        if (this.isStarted()) {
            return;
        }
        Log.i(TAG, "Starting");
        this.started = true;
        this.executor = Executors.newSingleThreadExecutor();
        this.executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.mainJob();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Log.i(TAG, "No longer listing");
            this.terminate();
        });
    }

    private void mainJob() throws InterruptedException {
        QueuedRequest request = this.requests.take();
        long ts = SystemClock.elapsedRealtime();
        if (!request.isReady(ts)) {
            this.requests.add(request);
            return;
        }
        request.runnable.run();
    }

    public void terminate() {
        if (!this.isStarted()) {
            return;
        }
        this.executor.shutdownNow();
        this.started = false;
        this.executor = null;
        Log.i(TAG, "Terminated");
    }

    public boolean isStarted() {
        return this.started;
    }


    static class QueuedRequest {
        long ts;
        long requestId;
        Runnable runnable;

        public QueuedRequest(long requestId, long scheduledTs, Runnable runnable) {
            this.ts = scheduledTs;
            this.requestId = requestId;
            this.runnable = runnable;
        }

        public boolean isReady(long ts) {
            return this.ts <= ts;
        }
    }

    public static class QueueComparator implements Comparator<QueuedRequest> {
        @Override
        public int compare(QueuedRequest x, QueuedRequest y) {
            return Long.compare(x.ts, y.ts);
        }
    }
}