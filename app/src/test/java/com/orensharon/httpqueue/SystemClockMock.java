package com.orensharon.httpqueue;

public class SystemClockMock implements ISystemClock {
    @Override
    public long getElapsedRealTime() {
        return System.currentTimeMillis();
    }
}
