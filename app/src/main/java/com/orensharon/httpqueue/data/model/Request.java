package com.orensharon.httpqueue.data.model;

public class Request {

    public interface Method {
        int PUT = 0;
    }

    private long id;
    private final String endpoint;
    private final String jsonPayload;
    private int retries;
    private long lastRetryMs;
    private int method;

    private Request(String endpoint, String jsonPayload, int method) {
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
        if (endpoint == null) {
            throw new NullPointerException("INVALID_ENDPOINT");
        }
        if (jsonPayload == null) {
            throw new NullPointerException("INVALID_PAYLOAD");
        }
        // TODO: validated payload
        // TODO: validate endpoint pattern
        return new Request(endpoint, jsonPayload, Method.PUT);
    }

    public void setId(long id) {
        if (this.id != 0) {
            throw new RuntimeException("ID_ALREADY_EXISTS");
        }
        this.id = id;
    }

    public void updateState(boolean success, long ts) {
        if (this.lastRetryMs > ts) {
            throw new RuntimeException("INVALID_TIMESTAMP");
        }
        if (!success) {
            this.failed(ts);
            return;
        }
        this.success();
    }

    public long getScheduledTime() {
        return this.lastRetryMs + this.calculateNextRetryInterval();
    }

    public boolean isSuccess() {
        // TODO: instead - add field 'success-ts'
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
            throw new RuntimeException("STATE_IMMUTABLE");
        }
        this.lastRetryMs = ts;
        this.retries++;
    }

    private void success() {
        if (this.isSuccess()) {
            return;
        }
        this.retries = -1;
    }

    private long calculateNextRetryInterval() {
        if (this.retries == 0) {
            return 0;
        }
        return (long) Math.pow(2, this.retries - 1) * 1000;
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
