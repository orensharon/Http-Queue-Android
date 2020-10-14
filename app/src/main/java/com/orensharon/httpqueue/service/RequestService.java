package com.orensharon.httpqueue.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.httpqueue.Constants;
import com.orensharon.httpqueue.data.model.Request;
import com.orensharon.httpqueue.data.RequestRepository;
import com.orensharon.httpqueue.data.event.RequestStateChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;

public class RequestService {

    private final static String TAG = RequestService.class.getSimpleName();

    private final RequestRepository repository;
    private final QueueWorker queueWorker;
    private final RequestDispatcher dispatcher;
    private final EventBus eventBus;
    private final Executor executor;

    public RequestService(RequestRepository repository, QueueWorker queueWorker, RequestDispatcher dispatcher, EventBus eventBus, Executor executor) {
        this.repository = repository;
        this.queueWorker = queueWorker;
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.executor = executor;
    }

    public void init() {
        this.executor.execute(() -> {
            this.repository.init();
            this.start();
        });
    }

    // Load old requests and start listening to new requests
    private void start() {
        this.queueWorker.listen();
        this.loadPastRequests();
    }

    // Add new request
    public void add(Request request) {
        this.executor.execute(() -> {
            this.repository.store(request);
            this.addToQueue(request);
        });
    }

    // Init all requests from repository into the queue
    private void loadPastRequests() {
        for (Request request : this.repository.list()) {
            this.addToQueue(request);
        }
    }

    // Add request to worker queue
    private void addToQueue(Request request) {
        if (request.isSuccess()) {
            // Already sent successfully
            return;
        }
        boolean backoffLimitReached = this.isBackoffLimitReached(request.getReties());
        if (backoffLimitReached) {
            Log.d(TAG, "addToQueue - Backoff limit reached: " + request.toString());
            return;
        }
        Runnable dequeueListener = () -> this.onRequestReady(request.getId());
        try {
            this.queueWorker.enqueue(request.getId(), request.getScheduledTime(), dequeueListener);
        } catch (Exception e) {
            e.printStackTrace();
            // Failed to add request into queue worker
            // TODO: What should I do about it?
        }
    }

    // Callback fired after request dequeue
    private void onRequestReady(long requestId) {
        this.executor.execute(() -> this.dispatchRequest(requestId));
    }

    // Send request
    private void dispatchRequest(long requestId) {
        Log.d(TAG, "onRequestReady requestId=" + requestId);
        Request request = this.repository.getById(requestId);
        int method = request.getMethod();
        String url = request.getEndpoint();
        String payload = request.getPayload();
        RequestDispatcher.Callback dispatchedCallback = (state) -> this.onDispatcherResponse(requestId, state);
        try {
            this.dispatcher.dispatch(method, url, payload, dispatchedCallback);
        } catch (Exception e) {
            // Failed to send request - add it back to dispatch queue
            // TODO: should I use retries form this kind of fails?
            this.addToQueue(request);
            e.printStackTrace();
        }
    }

    //Callback fired after receiving response
    private void onDispatcherResponse(long requestId, boolean success) {
        this.executor.execute(() -> this.handleRequestResult(requestId, success));
    }

    // Handle request result
    private void handleRequestResult(long requestId, boolean success) {
        Log.d(TAG, "onDispatcherResponse - requestId:" + requestId + " state: " + success);
        long ts = SystemClock.elapsedRealtime();
        Request request = this.repository.getById(requestId);
        request.updateState(success, ts);
        this.repository.store(request);
        long rtc = System.currentTimeMillis();
        this.eventBus.post(new RequestStateChangedEvent(requestId, rtc, success));
        if (!success) {
            // TODO: if 18th retry failed => delete request from db?
            this.addToQueue(request);
        }
        // TODO: delete success requests from repo?
    }

    private boolean isBackoffLimitReached(int retires) {
        return retires > Constants.MAX_BACKOFF_LIMIT - 1;
    }
}
