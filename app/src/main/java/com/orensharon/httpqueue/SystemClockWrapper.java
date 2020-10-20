package com.orensharon.httpqueue;

import android.os.SystemClock;

public class SystemClockWrapper implements ISystemClock {
    @Override
    public long getElapsedRealTime() {
        return SystemClock.elapsedRealtime();
    }
}
