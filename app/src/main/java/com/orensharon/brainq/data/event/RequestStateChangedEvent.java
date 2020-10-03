package com.orensharon.brainq.data.event;

import com.orensharon.brainq.util.Util;

public class RequestStateChangedEvent {
    public final long requestId;
    public final long ts;
    public final boolean state;

    public RequestStateChangedEvent(long requestId, long ts, boolean state) {
        this.requestId = requestId;
        this.ts = ts;
        this.state = state;
    }

    @Override
    public String toString() {
        return "RequestStateChangedEvent{" +
                "requestId=" + requestId +
                ", ts=" + Util.getDateStringByTimeStamp(this.ts) +
                ", state=" + state +
                '}';
    }
}