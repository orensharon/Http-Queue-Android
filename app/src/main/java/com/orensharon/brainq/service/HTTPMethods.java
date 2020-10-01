package com.orensharon.brainq.service;

import android.content.Context;

public interface HTTPMethods {

    interface Method {
        int PUT = 0;
    }

    public void put(Context context, String endpoint, String jsonPayload);
}

