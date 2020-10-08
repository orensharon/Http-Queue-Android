package com.orensharon.brainq.mock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orensharon.brainq.data.model.Request;
import com.orensharon.brainq.service.HttpQueueIntentService;

public class Receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, HttpQueueIntentService.class);
        i.putExtra("method", Request.Method.PUT);
        i.putExtra("endPoint", "https://jsonplaceholder.typicode.com/posts/1");
        i.putExtra("jsonPayload", Util.generatePayload());
        context.startService(i);
    }
}