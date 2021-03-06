package com.orensharon.httpqueue.presentation.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.orensharon.httpqueue.R;
import com.orensharon.httpqueue.data.model.Request;
import com.orensharon.httpqueue.databinding.ActivityVisualizationBinding;
import com.orensharon.httpqueue.mock.Util;
import com.orensharon.httpqueue.presentation.TimeScale;
import com.orensharon.httpqueue.presentation.model.RequestEvent;
import com.orensharon.httpqueue.presentation.vm.IVisualizationVM;
import com.orensharon.httpqueue.presentation.vm.VisualizationVM;
import com.orensharon.httpqueue.presentation.vm.VisualizationViewModelFactory;
import com.orensharon.httpqueue.service.HttpQueueIntentService;
import com.orensharon.httpqueue.presentation.util.DateUtil;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class VisualizationActivity extends AppCompatActivity {

    private final static String TAG = VisualizationActivity.class.getSimpleName();

    private LineGraphSeries<DataPoint> successSeries;
    private LineGraphSeries<DataPoint> failedSeries;
    private DateAsXAxisLabelFormatter minutelyFormat, hourlyFormat, dailyFormat, weeklyFormat;

    private ActivityVisualizationBinding binding;
    private IVisualizationVM viewModel;

    private GraphView graphView;

    @Inject
    VisualizationViewModelFactory visualizationViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_visualization);
        this.viewModel = new ViewModelProvider(this, this.visualizationViewModelFactory).get(VisualizationVM.class);
        this.binding.setLifecycleOwner(this);
        this.binding.setViewModel((VisualizationVM) this.viewModel);

        this.initObservers();
        this.viewModel.init();
        this.initGraphComponents();
        //Mock.startSendMock(this.getApplicationContext());
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
        Log.d(TAG, "appendNewEvent:" + event.toString());
        series.appendData(new DataPoint(new Date(event.ts), event.number), false, 1000);
    }

    private void sendInvalid() {
        this.send(Request.Method.PUT, Util.getInvalidURL(), Util.generatePayload());
    }

    private void sendValid() {
        this.send(Request.Method.PUT, Util.getValidURL(), Util.generatePayload());
    }

    private void send(int method, String endPoint, String payload) {
        Intent i = new Intent(this, HttpQueueIntentService.class);
        i.putExtra("method", method);
        i.putExtra("endPoint", endPoint);
        i.putExtra("jsonPayload", payload);
        this.startService(i);
    }

    private void applyTimeScale(int timeScale) {
        LabelFormatter labelFormatter = this.getLabelFormatter(timeScale);
        long start = this.viewModel.getStartTime();
        long end = this.viewModel.getEndTime();
        this.createGraphView(start, end, labelFormatter);
    }

    private void createGraphView(long start, long end, LabelFormatter labelFormatter) {
        if (this.graphView != null) {
            this.graphView.removeAllSeries();
            this.graphView = null;
        }
        this.binding.graphContainer.removeAllViews();
        GraphView graphView = new GraphView(this);
        graphView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        graphView.addSeries(this.successSeries);
        graphView.addSeries(this.failedSeries);
        graphView.getViewport().setMinX(start);
        graphView.getViewport().setMaxX(end);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setHumanRounding(false);
        graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphView.getGridLabelRenderer().setNumVerticalLabels(5);
        graphView.getGridLabelRenderer().setLabelFormatter(labelFormatter);
        this.binding.graphContainer.addView(graphView);
        this.graphView = graphView;
    }

    private void initGraphComponents() {
        // Init x axis label formatter
        this.minutelyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getMinutelyDateInFormat());
        this.hourlyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getHourlyDateInFormat());
        this.dailyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getDailyDateInFormat());
        this.weeklyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getWeeklyDateInFormat());
        // Series
        this.successSeries = this.initSeries(this.viewModel.getAllSuccessEvents());
        this.failedSeries = this.initSeries(this.viewModel.getAllFailedEvents());
        this.successSeries.setColor(Color.GREEN);
        this.failedSeries.setColor(Color.RED);
    }

    private LabelFormatter getLabelFormatter(int timeScale) {
        LabelFormatter labelFormatter = null;
        switch (timeScale) {
            case TimeScale.MINUTELY:
                labelFormatter = this.minutelyFormat;
                break;
            case TimeScale.HOURLY:
                labelFormatter = this.hourlyFormat;
                break;
            case TimeScale.DAILY:
                labelFormatter = this.dailyFormat;
                break;
            case TimeScale.WEEKLY:
                labelFormatter = this.weeklyFormat;
                break;
        }
        return labelFormatter;
    }

    private LineGraphSeries<DataPoint> initSeries(List<RequestEvent> events) {
        long x = this.viewModel.getStartTime();
        DataPoint[] dataPoints = new DataPoint[events.size() + 1];
        dataPoints[0] = new DataPoint(x, 0);
        for (int i = 0; i < dataPoints.length - 1; i++) {
            RequestEvent event = events.get(i);
            dataPoints[i + 1] = new DataPoint(event.ts, event.number);
        }
        return new LineGraphSeries<>(dataPoints);
    }

}