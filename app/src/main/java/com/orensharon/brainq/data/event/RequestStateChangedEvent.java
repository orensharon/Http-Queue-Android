package com.orensharon.brainq.data.event;

import java.util.Date;

public class RequestStateChangedEvent {
    public final int requestId;
    public final long ts;
    public final boolean state;

    public RequestStateChangedEvent(int requestId, long ts, boolean state) {
        this.requestId = requestId;
        this.ts = ts;
        this.state = state;
    }

    @Override
    public String toString() {
        return "RequestStateChangedEvent{" +
                "requestId=" + requestId +
                ", ts=" + new Date(this.ts).toString() +
                ", state=" + state +
                '}';
    }
}
