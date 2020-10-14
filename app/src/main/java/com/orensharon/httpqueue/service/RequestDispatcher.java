package com.orensharon.httpqueue.service;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class RequestDispatcher {

    private final static String TAG = RequestDispatcher.class.getSimpleName();

    private final RequestQueue requestQueue;

    public RequestDispatcher(RequestQueue requestQueue) {
        this.requestQueue = requestQueue;
    }

    public interface Callback {
        void onHandled(boolean success);
    }

    public void dispatch(int method, String url, String payload, Callback callback) throws JSONException {
        Log.d(TAG, "Dispatch url: " + url);
        int transformMethod = this.transformMethod(method);
        JSONObject body = new JSONObject(payload);
        JsonObjectRequest req = new JsonObjectRequest(
                transformMethod,
                url,
                body,
                response -> callback.onHandled(true),
                error -> callback.onHandled(false)
        );
        this.requestQueue.add(req);
    }

    private int transformMethod(int method) {
        switch (method) {
            case com.orensharon.httpqueue.data.model.Request.Method.PUT:
                return Request.Method.PUT;
            default:
                throw new RuntimeException("INVALID_METHOD");
        }
    }
}
