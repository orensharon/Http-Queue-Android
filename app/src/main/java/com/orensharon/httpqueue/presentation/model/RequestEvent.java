package com.orensharon.httpqueue.presentation.model;

public class RequestEvent {
    public final long requestId;
    public final boolean success;
    public final long ts;
    public final long number;

    public RequestEvent(long requestId, boolean success, long ts, long number) {
        this.requestId = requestId;
        this.success = success;
        this.ts = ts;
        this.number = number;
    }

    public RequestEvent(RequestEvent requestEvent) {
        this.requestId = requestEvent.requestId;
        this.success = requestEvent.success;
        this.ts = requestEvent.ts;
        this.number = requestEvent.number;
    }

    @Override
    public String toString() {
        return "RequestEvent{" +
                "requestId=" + requestId +
                ", state=" + success +
                ", ts=" + ts +
                ", number=" + number +
                '}';
    }
}
