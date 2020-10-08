package com.orensharon.brainq.presentation.model;


import java.util.ArrayList;
import java.util.List;

public class Visualization {
    private final List<RequestEvent> successEvents;
    private final List<RequestEvent> failedEvents;

    private final GraphTime graphTime;

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
        int successes = this.getEventFilteredByTime(this.successEvents);
        int fails =  this.getEventFilteredByTime(this.failedEvents);
        if (successes == 0 && fails == 0) {
            return -1;
        }
        return (int)(100 * ((double)successes / (successes + fails)));
    }

    private int getEventFilteredByTime(List<RequestEvent> events) {
        int counter = 0;
        for (RequestEvent event : events) {
            if (event.ts >= this.graphTime.getStart() && event.ts <= this.graphTime.getEnd()) {
                counter++;
            }
        }
        return counter;
    }

    public long getEndTime() {
        return this.graphTime.getEnd();
    }

    public long getStartTime() {
        return this.graphTime.getStart();
    }

    public int getTimeScale() {
        return this.graphTime.getTimeScale();
    }

    public List<RequestEvent> getAllFailedEventsImmutable() {
        return this.geEvents(this.failedEvents);
    }

    public List<RequestEvent> getAllSuccessEventsImmutable() {
        return this.geEvents(this.successEvents);
    }

    private List<RequestEvent> geEvents(List<RequestEvent> events) {
        List<RequestEvent> result = new ArrayList<>();
        for (RequestEvent requestEvent : events) {
            result.add(new RequestEvent(requestEvent));
        }
        return result;
    }
}
