package com.orensharon.brainq.presentation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.orensharon.brainq.App;
import com.orensharon.brainq.R;
import com.orensharon.brainq.data.event.RequestStateChangedEvent;
import com.orensharon.brainq.databinding.ActivityVisualizationBinding;
import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.presentation.vm.VisualizationVM;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpQueueIntentService;
import com.orensharon.brainq.util.DateUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

public class VisualizationActivity extends AppCompatActivity {

    private final static String TAG = VisualizationActivity.class.getSimpleName();

    private GraphView graphView;

    private PointsGraphSeries<DataPoint> successSeries;
    private PointsGraphSeries<DataPoint> failedSeries;
    private DateAsXAxisLabelFormatter hourlyFormat, dailyFormat, weeklyFormat;

    private ActivityVisualizationBinding binding;
    private VisualizationVM viewModel;

    @Inject
    EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)this.getApplicationContext()).applicationComponent.inject(this);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_visualization);
        this.viewModel = new ViewModelProvider(this, new ViewModelProvider.NewInstanceFactory()).get(VisualizationVM.class);
        this.binding.setLifecycleOwner(this);
        this.binding.setViewModel(this.viewModel);

        this.graphView = this.findViewById(R.id.graph);

        // Init x axises
        this.hourlyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getHourlyDateInFormat());
        this.dailyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getDailyDateInFormat());
        this.weeklyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getWeeklyDateInFormat());

        this.failedSeries = new PointsGraphSeries<>();
        this.failedSeries.setColor(Color.RED);
        this.failedSeries.setShape(PointsGraphSeries.Shape.POINT);

        this.successSeries = new PointsGraphSeries<>();
        this.successSeries.setColor(Color.GREEN);
        this.successSeries.setShape(PointsGraphSeries.Shape.TRIANGLE);

        this.graphView.addSeries(this.successSeries);
        this.graphView.addSeries(this.failedSeries);

        this.graphView.getViewport().setMinX(System.currentTimeMillis());
        this.graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        this.graphView.getGridLabelRenderer().setHumanRounding(false);
        this.graphView.getViewport().setXAxisBoundsManual(true);
        this.graphView.getViewport().setYAxisBoundsManual(true);
        this.graphView.getViewport().setMinY(0);
        this.graphView.getViewport().setMaxY(1);

        this.viewModel.getSendEvent().observe(this, type -> {
            if (type == 0) {
                this.sendValid();
                return;
            }
            this.sendInvalid();
        });
        this.viewModel.getTimeScale().observe(this, id -> {
            switch (id) {
                case R.id.hour:
                    this.applyHourly();
                    break;
                case R.id.day:
                    this.applyDaily();
                    break;
                case R.id.week:
                    this.applyWeekly();
                    break;
            }
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
        this.viewModel.addEvent(event.requestId, event.state, event.ts);
        if (event.state) {
            this.addSuccess(event.ts);
            return;
        }
        this.addFailed(event.ts);
    }

    private void sendInvalid() {
        this.send(HTTPMethods.Method.PUT, "https://jsonplaceholder.typicode.com/posts/122", Util.generatePayload());
    }

    private void sendValid() {
        this.send(HTTPMethods.Method.PUT, "https://jsonplaceholder.typicode.com/posts/1", Util.generatePayload());
    }

    private void send(int method, String endPoint, String payload) {
        Intent i = new Intent(this, HttpQueueIntentService.class);
        i.putExtra("method", method);
        i.putExtra("endPoint", endPoint);
        i.putExtra("jsonPayload", payload);
        this.startService(i);
    }

    private void addFailed(long  ts) {
        this.failedSeries.appendData(new DataPoint(new Date(ts), 0), false, 60);
    }

    private void addSuccess(long ts) {
        this.successSeries.appendData(new DataPoint(new Date(ts), 1), false, 60);
    }

    private void applyHourly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        this.graphView.getGridLabelRenderer().setLabelFormatter(this.hourlyFormat);
        this.graphView.getViewport().setMaxX(calendar.getTimeInMillis());
    }

    private void applyDaily() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        this.graphView.getGridLabelRenderer().setLabelFormatter(this.dailyFormat);
        this.graphView.getViewport().setMaxX(calendar.getTimeInMillis());
    }

    private void applyWeekly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        this.graphView.getGridLabelRenderer().setLabelFormatter(this.weeklyFormat);
        this.graphView.getViewport().setMaxX(calendar.getTimeInMillis());
    }
}