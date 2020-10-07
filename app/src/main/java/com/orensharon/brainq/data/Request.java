package com.orensharon.brainq.data;

import com.orensharon.brainq.service.HTTPMethods;

import java.io.Serializable;

public class Request implements Serializable {

    private long id;
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

    public Request(long id, String endpoint, String jsonPayload, int method, int retries, long lastRetryMs) {
        this.id = id;
        this.endpoint = endpoint;
        this.jsonPayload = jsonPayload;
        this.method = method;
        this.retries = retries;
        this.lastRetryMs = lastRetryMs;
    }

    public Request(Request request) {
        this.id = request.id;
        this.endpoint = request.endpoint;
        this.jsonPayload = request.jsonPayload;
        this.method = request.method;
        this.retries = request.retries;
        this.lastRetryMs = request.lastRetryMs;
    }

    public static Request put(String endpoint, String jsonPayload) {
        return new Request(endpoint, jsonPayload, HTTPMethods.Method.PUT);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void updateState(boolean state, long ts) {
        if (!state) {
            this.failed(ts);
            return;
        }
        this.success();
    }

    public long getScheduledTime() {
        return this.lastRetryMs + this.calculateNextRetryInterval();
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

    public long getId() {
        return id;
    }

    public int getReties() {
        return this.retries;
    }

    public long getLastRetryMs() {
        return lastRetryMs;
    }

    private void failed(long ts) {
        if (this.isSuccess()) {
            return;
        }
        this.lastRetryMs = ts;
        this.retries++;
    }

    private void success() {
        this.retries = -1;
    }

    private long calculateNextRetryInterval() {
        return (long) Math.pow(2, this.retries) * 1000;
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
