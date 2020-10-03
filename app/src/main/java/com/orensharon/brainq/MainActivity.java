package com.orensharon.brainq;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Button;

import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpQueueIntentService;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private HttpQueueIntentService service;
    private boolean bounded = false;

    private Button sendValidButton;
    private Button sendInvalidButton;

    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            HttpQueueIntentService.LocalBinder binder = (HttpQueueIntentService.LocalBinder) service;
            MainActivity.this.service = binder.getService();
            bounded = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bounded = false;
            unbindService(connection);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((App)this.getApplicationContext()).applicationComponent.inject(this);

        this.sendValidButton = this.findViewById(R.id.sendValidButton);
        this.sendInvalidButton = this.findViewById(R.id.sendInvalidButton);

        this.sendValidButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HttpQueueIntentService.class);
            i.putExtra("method", HTTPMethods.Method.PUT);
            i.putExtra("endPoint", "https://jsonplaceholder.typicode.com/posts/1");
            i.putExtra("jsonPayload", Util.generatePayload());
            this.startService(i);
        });

        this.sendInvalidButton.setOnClickListener(v -> {
            Intent i = new Intent(this, HttpQueueIntentService.class);
            i.putExtra("method", HTTPMethods.Method.PUT);
            i.putExtra("endPoint", "https://jsonplaceholder.typicode.com/posts/122");
            i.putExtra("jsonPayload", Util.generatePayload());
            this.startService(i);
        });
        //Mock.startSendMock(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = new Intent(this, HttpQueueIntentService.class);
//        this.bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        this.unbindService(this.connection);
    }
}