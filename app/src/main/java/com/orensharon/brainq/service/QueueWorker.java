package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueueWorker {

    private final static String TAG = QueueWorker.class.getSimpleName();


    private volatile boolean started;

    private final ExecutorService executor;
    private final RequestRepository repository;
    private final RequestQueue requestQueue;

    private RequestStateListener listener;

    public QueueWorker(RequestQueue requestQueue, RequestRepository repository) {
        this.repository = repository;
        this.requestQueue = requestQueue;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void start() {
        if (this.isStarted()) {
            return;
        }
        Log.i(TAG, "starting");
        this.started = true;
        this.executor.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Request request = this.repository.take();
                    long ts = SystemClock.elapsedRealtime();
                    Log.i(TAG, "Checking request: " + request.toString());
                    if (request.isReady(ts)) {
                        this.sendRequest(request);
                    } else {
                        this.repository.add(request);
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stop() {
        if (!this.isStarted()) {
            return;
        }
        this.executor.shutdown();
        this.started = false;
    }

    public boolean isStarted() {
        return this.started;
    }

    private void sendRequest(Request request) {
        if (request == null) {
            return;
        }
        if (this.listener == null) {
            return;
        }
        long ts = SystemClock.elapsedRealtime();
        if (!request.isReady(ts)) {
            return;
        }
        Log.i(TAG, "Sending request: " + request.toString());
        // TODO: volley response on main thread!
        try {
            JsonObjectRequest req = new JsonObjectRequest(
                    request.getMethod(),
                    request.getEndpoint(),
                    new JSONObject(request.getPayload()),
                    response -> {
                        request.success();
                        Log.i(TAG, "Request success: " + request.toString());
                        this.listener.onRequestStateChange(request);
                    },
                    error -> {
                        request.failed(ts);
                        Log.i(TAG, "Request failed: " + request.toString());
                        this.repository.add(request);
                        this.listener.onRequestStateChange(request);
                    });
            this.requestQueue.add(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setListener(RequestStateListener listener) {
        this.listener = listener;
    }
}
