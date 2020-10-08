package com.orensharon.httpqueue.service;

import android.content.Context;

public interface HTTPMethods {
    void put(Context context, String endpoint, String jsonPayload);
}

