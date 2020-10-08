package com.orensharon.httpqueue.presentation.model;

import com.orensharon.httpqueue.presentation.TimeScale;

import java.util.Calendar;

public class GraphTime {
    private long start;
    private long end;
    private int timeScale;

    public GraphTime(int timeScale) {
        // TODO: calendar get instance heavy?
        this.start = Calendar.getInstance().getTimeInMillis();
        this.timeScale = timeScale;
        this.end = this.computeEnd(timeScale);
    }

    public GraphTime(GraphTime graphTime) {
        this.start = graphTime.start;
        this.end = graphTime.end;
        this.timeScale = graphTime.timeScale;
    }

    public void setTimeScale(int timeScale) {
        this.timeScale = timeScale;
        this.end = this.computeEnd(timeScale);
    }

    public int getTimeScale() {
        return timeScale;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    private long computeEnd(int timeScale) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(this.start);
        switch (timeScale) {
            case TimeScale.MINUTELY:
                calendar.add(Calendar.MINUTE, 1);
                break;
            case TimeScale.HOURLY:
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case TimeScale.DAILY:
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                break;
            case TimeScale.WEEKLY:
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                break;
        }
        return calendar.getTimeInMillis();
    }
}
