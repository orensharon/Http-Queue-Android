package com.orensharon.brainq.data;

import com.orensharon.brainq.service.HTTPMethods;

import java.io.Serializable;

public class Request implements Serializable {

    private final int id;
    private final String endpoint;
    private final String jsonPayload;
    private int retries;
    private long lastRetryMs;
    private int method;

    public Request(int id, String endpoint, String jsonPayload, int method) {
        this.id = id;
        this.endpoint = endpoint;
        this.jsonPayload = jsonPayload;
        this.method = method;
        this.retries = 0;
        this.lastRetryMs = 0;
    }

    public Request(Request request) {
        this.id = request.id;
        this.endpoint = request.endpoint;
        this.jsonPayload = request.jsonPayload;
        this.method = request.method;
        this.retries = request.retries;
        this.lastRetryMs = request.lastRetryMs;
    }

    public static Request put(int id, String endpoint, String jsonPayload) {
        return new Request(id ,endpoint, jsonPayload, HTTPMethods.Method.PUT);
    }

    public void failed(long ts) {
        if (this.isSuccess()) {
            return;
        }
        this.lastRetryMs = ts;
        this.retries++;
    }

    public void success() {
        this.retries = -1;
    }

    public boolean isReady(long ts) {
        long timeElapsed = ts - this.lastRetryMs;
        long timeToWait = (long) Math.pow(2, retries) * 1000;
        return timeElapsed >= timeToWait;
    }

    public boolean isSuccess() {
        return this.retries == -1;
    }

    public int getMethod() {
        return method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public String getPayload() {
        return jsonPayload;
    }

    public int getRetries() {
        return retries;
    }

    public long getLastRetryMs() {
        return lastRetryMs;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Request{" +
                "id=" + id +
                ", retries=" + retries +
                ", lastRetryMs=" + lastRetryMs +
                '}';
    }
}
