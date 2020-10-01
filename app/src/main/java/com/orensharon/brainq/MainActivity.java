package com.orensharon.brainq;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;

import com.orensharon.brainq.mock.Mock;
import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpService;

public class MainActivity extends AppCompatActivity {

    private HttpService service;
    private boolean bounded = false;

    public static final String REQUEST_STATE = "REQUEST_STATE";

    private Button sendValidButton;
    private Button sendInvalidButton;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            HttpService.LocalBinder binder = (HttpService.LocalBinder) service;
            MainActivity.this.service = binder.getService();
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.sendValidButton = this.findViewById(R.id.sendValidButton);
        this.sendInvalidButton = this.findViewById(R.id.sendInvalidButton);

        this.sendValidButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HttpService.class);
            i.putExtra("method", HTTPMethods.Method.PUT);
            i.putExtra("endPoint", "https://jsonplaceholder.typicode.com/posts/1");
            i.putExtra("jsonPayload", Util.generatePayload());
            this.startService(i);
        });

        this.sendInvalidButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HttpService.class);
            i.putExtra("method", HTTPMethods.Method.PUT);
            i.putExtra("endPoint", "https://jsonplaceholder.typicode.com/posts/122");
            i.putExtra("jsonPayload", Util.generatePayload());
            this.startService(i);
        });

        Mock.startSendMock(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, HttpService.class);
        this.bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unbindService(this.connection);
    }
}