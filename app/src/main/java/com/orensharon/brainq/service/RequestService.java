package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.BrainQ;
import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.data.event.RequestStateChangedEvent;

import org.greenrobot.eventbus.EventBus;

public class RequestService {

    private final static String TAG = RequestService.class.getSimpleName();

    private final RequestRepository repository;
    private final QueueManager queueManager;
    private final RequestDispatcher dispatcher;
    private final EventBus eventBus;

    public RequestService(RequestRepository repository, QueueManager queueManager, RequestDispatcher dispatcher, EventBus eventBus) {
        this.repository = repository;
        this.queueManager = queueManager;
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
    }

    public void start() {
        this.queueManager.listen();
    }

    public void add(Request request) {
        this.repository.store(request);
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

    private void onRequestReady(long requestId) {
        Log.i(TAG, "onRequestReady requestId=" + requestId);
        Request request = this.repository.getById(requestId);
        int method = request.getMethod();
        String url = request.getEndpoint();
        String payload = request.getPayload();
        RequestDispatcher.Callback dispatchedCallback = (state) -> this.onDispatcherResponse(requestId, state);
        this.dispatcher.dispatch(method, url, payload, dispatchedCallback);
    }

    private void onDispatcherResponse(long requestId, boolean state) {
        Log.i(TAG, "onDispatcherResponse - requestId:" + requestId + " state: " + state);
        long ts = SystemClock.elapsedRealtime();
        Request request = this.repository.getById(requestId);
        request.updateState(state, ts);
        this.repository.store(request);
        long rtc = System.currentTimeMillis();
        this.eventBus.post(new RequestStateChangedEvent(requestId, rtc, state));
        if (state) {
            return;
        }
        this.addToQueue(request);
    }

    private boolean isBackoffLimitReached(int retires) {
        return retires > BrainQ.MAX_BACKOFF_LIMIT - 1;
    }
}
