package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.orensharon.BrainQ;
import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.data.event.RequestStateChangedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
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

    public void start() {
        this.executor.execute(() -> {
            this.repository.init();
            this.queueWorker.listen();
            List<Request> requests = this.repository.list();
            for (Request request : requests) {
                this.addToQueue(request);
            }
        });
    }

    public void add(Request request) {
        this.executor.execute(() -> {
            this.repository.store(request);
            this.addToQueue(request);
        });
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
        this.queueWorker.enqueue(request.getId(), request.getScheduledTime(), dequeueListener);
    }

    private void onRequestReady(long requestId) {
        this.executor.execute(() -> {
            Log.i(TAG, "onRequestReady requestId=" + requestId);
            Request request = this.repository.getById(requestId);
            int method = request.getMethod();
            String url = request.getEndpoint();
            String payload = request.getPayload();
            RequestDispatcher.Callback dispatchedCallback = (state) -> this.onDispatcherResponse(requestId, state);
            this.dispatcher.dispatch(method, url, payload, dispatchedCallback);
        });
    }

    private void onDispatcherResponse(long requestId, boolean state) {
        this.executor.execute(() -> {
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
        });
    }

    private boolean isBackoffLimitReached(int retires) {
        return retires > BrainQ.MAX_BACKOFF_LIMIT - 1;
    }
}
