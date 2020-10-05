package com.orensharon.brainq.presentation.model;

public class RequestEvent {
    public final int requestId;
    public final boolean state;
    public final long ts;
    public final long number;

    public RequestEvent(int requestId, boolean state, long ts, long number) {
        this.requestId = requestId;
        this.state = state;
        this.ts = ts;
        this.number = number;
    }
}
