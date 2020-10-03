package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;

import org.jetbrains.annotations.NotNull;

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
        if (request.isSuccess() || request.isBackoffLimitReached()) {
            Log.i(TAG, "addToQueue - back-off limit reached: " + request.toString());
            return;
        }
        this.queueManager.add(request.getId(), request.getScheduledTs(), this.onRequestReady(request.getId()));
    }

    @NotNull
    private Runnable onRequestReady(int requestId) {
        return () -> {
            Log.i(TAG, "onRequestReady requestId=" + requestId);
            Request request = this.repository.getById(requestId);
            int method = request.getMethod();
            String url = request.getEndpoint();
            String payload = request.getPayload();
            this.dispatcher.dispatch(method, url, payload, this.onDispatcherResponse(requestId));
        };
    }

    @NotNull
    private RequestDispatcher.Callback onDispatcherResponse(int requestId) {
        return state -> {
            long ts = SystemClock.elapsedRealtime();
            Request request = this.repository.getById(requestId);
            // TODO: make sure dispatched state?
            if (!state) {
                request.failed(ts);
                Log.i(TAG, "onDispatcherResponse - Request failed: " + request.toString());
            } else {
                request.success();
                Log.i(TAG, "onDispatcherResponse - Request success: " + request.toString());
            }
            this.repository.save(request);
            if (!state) {
                this.addToQueue(request);
            }
        };
    }
}
