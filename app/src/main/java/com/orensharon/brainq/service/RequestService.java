package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.BrainQ;
import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;

public class RequestService {

    private final static String TAG = RequestService.class.getSimpleName();

    private final RequestRepository repository;
    private final QueueManager queueManager;
    private final RequestDispatcher dispatcher;

    public RequestService(RequestRepository repository, QueueManager queueManager, RequestDispatcher dispatcher) {
        this.repository = repository;
        this.queueManager = queueManager;
        this.dispatcher = dispatcher;
    }

    public void start() {
        this.queueManager.listen();
    }

    public void add(Request request) {
        this.repository.add(request);
        this.addToQueue(request);
    }

    private void addToQueue(Request request) {
        if (request.isSuccess()) {
            // Already sent successfully
            return;
        }
        boolean backoffLimitReached = this.isBackoffLimitReached(request.getReties());
        if (backoffLimitReached) {
            Log.i(TAG, "addToQueue - Backoff limit reached: " + request.toString());
            return;
        }
        Runnable dequeueListener = () -> this.onRequestReady(request.getId());
        this.queueManager.enqueue(request.getId(), request.getScheduledTime(), dequeueListener);
    }

    private void onRequestReady(int requestId) {
        Log.i(TAG, "onRequestReady requestId=" + requestId);
        Request request = this.repository.getById(requestId);
        int method = request.getMethod();
        String url = request.getEndpoint();
        String payload = request.getPayload();
        RequestDispatcher.Callback dispatchedCallback = (state) -> this.onDispatcherResponse(requestId, state);
        this.dispatcher.dispatch(method, url, payload, dispatchedCallback);
    }

    private void onDispatcherResponse(int requestId, boolean state) {
        long ts = SystemClock.elapsedRealtime();
        Request request = this.repository.getById(requestId);
        request.updateState(state, ts);
        this.repository.save(request);
        if (state) {
            Log.i(TAG, "onDispatcherResponse - Request success: " + request.toString());
            return;
        }
        // State failed - add back to queue
        Log.i(TAG, "onDispatcherResponse - Request failed: " + request.toString());
        this.addToQueue(request);
    }

    private boolean isBackoffLimitReached(int retires) {
        return retires > BrainQ.MAX_BACKOFF_LIMIT - 1;
    }
}
