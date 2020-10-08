package com.orensharon.brainq.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.orensharon.brainq.App;
import com.orensharon.brainq.data.model.Request;

import javax.inject.Inject;

public class HttpQueueIntentService extends IntentService implements HTTPMethods {


    private final static String TAG = HttpQueueIntentService.class.getSimpleName();

    private final IBinder binder = new LocalBinder();

    @Inject
    RequestService requestService;

    public HttpQueueIntentService() {
        this("HttpQueueIntentService");
    }

    public HttpQueueIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        super.onCreate();
        ((App)this.getApplicationContext()).applicationComponent.inject(this);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "onHandleIntent");
        if (intent == null) {
            return;
        }
        String endPoint = intent.getStringExtra("endPoint");
        String jsonPayload = intent.getStringExtra("jsonPayload");
        int method = intent.getIntExtra("method", -1);
        switch (method) {
            case Request.Method.PUT:
                this.put(this, endPoint, jsonPayload);
                break;
            default:
                break;
        }
    }

    @Override
    public void put(Context context, String endpoint, String jsonPayload) {
        Request request = Request.put(endpoint, jsonPayload);
        Log.i(TAG, "put: " + request.toString());
        this.requestService.add(request);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public HttpQueueIntentService getService() {
            return HttpQueueIntentService.this;
        }
    }
}
