package com.orensharon;

import com.orensharon.brainq.BuildConfig;

public interface BrainQ {
    interface TimeScale {
        int HOURLY = 0;
        int DAILY = 1;
        int WEEKLY = 2;
    }
    // X exponential retries;
    int MAX_BACKOFF_LIMIT = BuildConfig.BACKOFF_LIMIT;
}
