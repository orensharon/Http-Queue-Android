package com.orensharon.brainq.service;

import com.orensharon.brainq.data.Request;

public interface RequestStateListener {
    interface State {
        int SUCCESS = 0;
        int FAILED = 1;
    }
    void onRequestStateChange(Request request);
}
