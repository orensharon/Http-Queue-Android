package com.orensharon.brainq.presentation.model;

import java.util.ArrayList;
import java.util.List;

public class Visualization {
    private final List<RequestEvent> successEvents;
    private final List<RequestEvent> failedEvents;

    public Visualization() {
        this.successEvents = new ArrayList<>();
        this.failedEvents = new ArrayList<>();
    }

    public RequestEvent add(long requestId, boolean state, long ts) {
        List<RequestEvent> list = state? this.successEvents: this.failedEvents;
        long eventNumber = list.size() + 1;
        RequestEvent event = new RequestEvent(requestId, state, ts, eventNumber);
        list.add(event);
        return event;
    }
}
