package com.orensharon.httpqueue;

import com.orensharon.httpqueue.service.RequestDispatcher;

import org.json.JSONException;

public class DispatcherStub extends RequestDispatcher {

    private boolean success;

    public DispatcherStub() {
        super(null);
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public void dispatch(int method, String url, String payload, Callback callback) throws JSONException {
        callback.onHandled(this.success);
    }
}
