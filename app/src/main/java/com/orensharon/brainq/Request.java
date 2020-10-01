package com.orensharon.brainq;

import com.orensharon.brainq.service.HTTPMethods;

import java.io.Serializable;

public class Request implements Serializable {
    private final String endpoint;
    private final String jsonPayload;
    private int retries;
    private long lastRetryMs;
    private int method;

    public Request(String endpoint, String jsonPayload, int method) {
        this.endpoint = endpoint;
        this.jsonPayload = jsonPayload;
        this.method = method;
        this.retries = 0;
        this.lastRetryMs = 0;
    }

    public static Request put(String endpoint, String jsonPayload) {
        return new Request(endpoint, jsonPayload, HTTPMethods.Method.PUT);
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

    @Override
    public String toString() {
        return "Request{" +
                "endpoint='" + endpoint + '\'' +
                ", jsonPayload='" + jsonPayload + '\'' +
                ", retries=" + retries +
                ", lastRetryMs=" + lastRetryMs +
                ", lastRetryMs=" + lastRetryMs +
                ", method=" + method +
                '}';
    }
}
