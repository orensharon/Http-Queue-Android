package com.orensharon;

import com.orensharon.brainq.BuildConfig;

public interface BrainQ {
    // X exponential retries;
    int MAX_BACKOFF_LIMIT = BuildConfig.BACKOFF_LIMIT;
}
