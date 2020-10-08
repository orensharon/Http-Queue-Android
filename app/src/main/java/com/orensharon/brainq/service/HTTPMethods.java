package com.orensharon.brainq.service;

import android.content.Context;

public interface HTTPMethods {
    void put(Context context, String endpoint, String jsonPayload);
}

