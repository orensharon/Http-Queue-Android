package com.orensharon.brainq.presentation.model;


import java.util.ArrayList;
import java.util.List;

public class Visualization {
    private final List<RequestEvent> successEvents;
    private final List<RequestEvent> failedEvents;

    private GraphTime graphTime;

    // TODO: get success ratio by time

    public Visualization(int timeScale) {
        this.successEvents = new ArrayList<>();
        this.failedEvents = new ArrayList<>();
        this.graphTime = new GraphTime(timeScale);
    }

    public RequestEvent add(long requestId, boolean state, long ts) {
        List<RequestEvent> list = state? this.successEvents: this.failedEvents;
        long eventNumber = list.size() + 1;
        RequestEvent event = new RequestEvent(requestId, state, ts, eventNumber);
        list.add(event);
        return event;
    }

    public void changeTimeScale(int timeScale) {
        this.graphTime.setTimeScale(timeScale);
    }

    public int getSuccessRatio() {
        int successes =  this.successEvents.size();
        int fails =  this.failedEvents.size();
        if (successes == 0 && fails == 0) {
            return -1;
        }
        return (int)(100 * ((double)successes / (successes + fails)));
    }

    public GraphTime getGraphTime() {
        return new GraphTime(this.graphTime);
    }
}
