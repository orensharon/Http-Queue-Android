package com.orensharon.httpqueue.data.event;

import java.util.Date;

public class RequestStateChangedEvent {
    public final long requestId;
    public final long ts;
    public final boolean success;

    public RequestStateChangedEvent(long requestId, long ts, boolean success) {
        this.requestId = requestId;
        this.ts = ts;
        this.success = success;
    }

    @Override
    public String toString() {
        return "RequestStateChangedEvent{" +
                "requestId=" + requestId +
                ", ts=" + new Date(this.ts).toString() +
                ", success=" + success +
                '}';
    }
}
