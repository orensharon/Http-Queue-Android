package com.orensharon.brainq.service;

import android.os.SystemClock;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.RequestRepository;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequestQueue {

    private final static String TAG = HttpRequestQueue.class.getSimpleName();


    private volatile boolean started;

    private ExecutorService executor;
    private final RequestRepository repository;
    private final RequestQueue requestQueue;

    private RequestStateListener listener;

    public HttpRequestQueue(RequestQueue requestQueue, RequestRepository repository) {
        this.repository = repository;
        this.requestQueue = requestQueue;
    }

    public void listen() {
        if (this.isStarted()) {
            return;
        }
        Log.i(TAG, "starting");
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

    public void terminate() {
        if (!this.isStarted()) {
            return;
        }
        this.executor.shutdownNow();
        this.started = false;
        this.listener = null;
        this.executor = null;
        Log.i(TAG, "terminated");
    }

    public boolean isStarted() {
        return this.started;
    }

    private void mainJob() throws InterruptedException {
        Request request = this.repository.take();
        long ts = SystemClock.elapsedRealtime();
        if (request.isReady(ts)) {
            this.sendRequest(request);
            return;
        }
        this.repository.add(request);
    }

    private void sendRequest(Request request) {
        if (request == null) {
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
                        if (this.listener != null) {
                            this.listener.onRequestStateChange(request);
                        }
                    },
                    error -> {
                        long ts = SystemClock.elapsedRealtime();
                        request.failed(ts);
                        Log.i(TAG, "Request failed: " + request.toString());
                        this.repository.add(request);
                        if (this.listener != null) {
                            this.listener.onRequestStateChange(request);
                        }
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
