package com.orensharon.httpqueue.presentation.model;

public class RequestEvent {
    public final long requestId;
    public final boolean state;
    public final long ts;
    public final long number;

    public RequestEvent(long requestId, boolean state, long ts, long number) {
        this.requestId = requestId;
        this.state = state;
        this.ts = ts;
        this.number = number;
    }

    public RequestEvent(RequestEvent requestEvent) {
        this.requestId = requestEvent.requestId;
        this.state = requestEvent.state;
        this.ts = requestEvent.ts;
        this.number = requestEvent.number;
    }

    @Override
    public String toString() {
        return "RequestEvent{" +
                "requestId=" + requestId +
                ", state=" + state +
                ", ts=" + ts +
                ", number=" + number +
                '}';
    }
}
