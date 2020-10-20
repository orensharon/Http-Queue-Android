package com.orensharon.httpqueue.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.httpqueue.ISystemClock;

import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.PriorityBlockingQueue;

import javax.inject.Inject;

public class QueueWorker {

    private final static String TAG = QueueWorker.class.getSimpleName();
    private final static long DELAY_BETWEEN_ENQUEUES = 100L;

    private volatile boolean started;

    private ExecutorService executor;
    private final ISystemClock systemClock;
    private final PriorityBlockingQueue<QueuedRequest> requests;

    public interface Listener {
        void run(long requestId);
    }

    @Inject
    public QueueWorker(ExecutorService executor, ISystemClock systemClock) {
        // TODO: size
        this.executor = executor;
        this.systemClock = systemClock;
        this.requests = new PriorityBlockingQueue<>(10, new QueueComparator());
    }


    public void enqueue(long requestId, long scheduledTs, Listener listener) {
        if (listener == null) {
            throw new NullPointerException("DEQUEUE_LISTENER_NULL");
        }
        if (!this.isStarted()) {
            throw new RuntimeException("WORKER_NOT_STARTED");
        }
        this.requests.add(new QueuedRequest(requestId, scheduledTs, listener));
    }

    public void listen() {
        if (this.isStarted()) {
            throw new RuntimeException("ALREADY_LISTENING");
        }
        Log.d(TAG, "Starting");
        this.started = true;
        Runnable runnable = () -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    this.mainJob();
                    Thread.sleep(DELAY_BETWEEN_ENQUEUES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
            Log.d(TAG, "No longer listing");
            // TODO: restart executor if state is started
            this.terminate();
        };
        this.executor.execute(runnable);
    }

    private void mainJob() throws InterruptedException {
        QueuedRequest request = this.requests.take();
        long ts = this.systemClock.getElapsedRealTime();
        if (!request.isReady(ts)) {
            this.requests.add(request);
            return;
        }
        request.listener.run(request.requestId);
    }

    public void terminate() {
        if (!this.isStarted()) {
            return;
        }
        this.executor.shutdownNow();
        this.started = false;
        this.executor = null;
        Log.d(TAG, "Terminated");
    }

    public boolean isStarted() {
        return this.started;
    }


    static class QueuedRequest {
        final long ts;
        final long requestId;
        final Listener listener;

        public QueuedRequest(long requestId, long scheduledTs, Listener listener) {
            this.ts = scheduledTs;
            this.requestId = requestId;
            this.listener = listener;
        }

        public boolean isReady(long ts) {
            return this.ts <= ts;
        }
    }

    private static class QueueComparator implements Comparator<QueuedRequest> {
        @Override
        public int compare(QueuedRequest x, QueuedRequest y) {
            return Long.compare(x.ts, y.ts);
        }
    }
}
