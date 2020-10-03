package com.orensharon.brainq;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.orensharon.brainq.data.event.RequestStateChangedEvent;
import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpQueueIntentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private Button sendValidButton;
    private Button sendInvalidButton;
    private GraphView graphView;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((App)this.getApplicationContext()).applicationComponent.inject(this);

        this.sendValidButton = this.findViewById(R.id.sendValidButton);
        this.sendInvalidButton = this.findViewById(R.id.sendInvalidButton);
        this.graphView = this.findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setColor(Color.RED);
        this.graphView.addSeries(series);
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 2),
                new DataPoint(1, 5),
                new DataPoint(2, 1),
                new DataPoint(3, 3),
                new DataPoint(4, 4)
        });
        series1.setColor(Color.BLUE);
        this.graphView.addSeries(series1);

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
    protected void onResume() {
        super.onResume();
        this.eventBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestStateChangedEvent(RequestStateChangedEvent event) {
        Log.i(TAG, "onRequestStateChangedEvent " + event.toString());
    }
}