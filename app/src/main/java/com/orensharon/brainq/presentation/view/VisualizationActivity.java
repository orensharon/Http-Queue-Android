package com.orensharon.brainq.presentation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.orensharon.BrainQ;
import com.orensharon.brainq.App;
import com.orensharon.brainq.R;
import com.orensharon.brainq.databinding.ActivityVisualizationBinding;
import com.orensharon.brainq.mock.Mock;
import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.presentation.model.RequestEvent;
import com.orensharon.brainq.presentation.vm.VisualizationVM;
import com.orensharon.brainq.presentation.vm.VisualizationViewModelFactory;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpQueueIntentService;
import com.orensharon.brainq.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;


public class VisualizationActivity extends AppCompatActivity {

    private final static String TAG = VisualizationActivity.class.getSimpleName();

    private GraphView graphView;

    private LineGraphSeries<DataPoint> successSeries;
    private LineGraphSeries<DataPoint> failedSeries;
    private DateAsXAxisLabelFormatter hourlyFormat, dailyFormat, weeklyFormat;

    private ActivityVisualizationBinding binding;
    private VisualizationVM viewModel;

    private long start;

    @Inject
    VisualizationViewModelFactory visualizationViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App)this.getApplicationContext()).applicationComponent.inject(this);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_visualization);
        this.viewModel = new ViewModelProvider(this, this.visualizationViewModelFactory).get(VisualizationVM.class);
        this.binding.setLifecycleOwner(this);
        this.binding.setViewModel(this.viewModel);

        this.graphView = binding.graph;

        // Init x axises
        this.hourlyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getHourlyDateInFormat());
        this.dailyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getDailyDateInFormat());
        this.weeklyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getWeeklyDateInFormat());

        Calendar calendar = Calendar.getInstance();
        this.start = calendar.getTimeInMillis();

        this.successSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(new Date(this.start), 0)
        });
        this.failedSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(new Date(this.start), 0)
        });
        this.successSeries.setColor(Color.GREEN);
        this.failedSeries.setColor(Color.RED);

        this.graphView.addSeries(this.successSeries);
        this.graphView.addSeries(this.failedSeries);

        this.graphView.getViewport().setMinX(this.start);
        this.graphView.getGridLabelRenderer().setNumHorizontalLabels(3);
        this.graphView.getViewport().setXAxisBoundsManual(true);

        this.initObservers();
        Mock.startSendMock(this.getApplicationContext());
    }

    private void initObservers() {
        this.viewModel.getValidClick().observe(this, b -> this.sendValid());
        this.viewModel.getInvalidClick().observe(this, b -> this.sendInvalid());
        this.viewModel.getLastSuccessEvent().observe(this, this::appendSuccessEvent);
        this.viewModel.getLastFailedEvent().observe(this, this::appendFailedEvent);
        this.viewModel.getTimeScale().observe(this, this::applyTimeScale);
    }

    private void appendSuccessEvent(RequestEvent event) {
        this.appendNewEvent(this.successSeries, event);
    }

    private void appendFailedEvent(RequestEvent event) {
        this.appendNewEvent(this.failedSeries, event);
    }

    private void appendNewEvent(LineGraphSeries<DataPoint> series, RequestEvent event) {
        series.appendData(new DataPoint(new Date(event.ts), event.number), false, 1000);
    }

    private void sendInvalid() {
        this.send(HTTPMethods.Method.PUT, Util.getInvalidURL(), Util.generatePayload());
    }

    private void sendValid() {
        this.send(HTTPMethods.Method.PUT, Util.getValidURL(), Util.generatePayload());
    }

    private void send(int method, String endPoint, String payload) {
        Intent i = new Intent(this, HttpQueueIntentService.class);
        i.putExtra("method", method);
        i.putExtra("endPoint", endPoint);
        i.putExtra("jsonPayload", payload);
        this.startService(i);
    }

    private void applyTimeScale(int  timeScale) {
        switch (timeScale) {
            case BrainQ.TimeScale.HOURLY:
                this.applyHourly();
                break;
            case BrainQ.TimeScale.DAILY:
                this.applyDaily();
                break;
            case BrainQ.TimeScale.WEEKLY:
                this.applyWeekly();
                break;
        }
    }

    private void applyHourly() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
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