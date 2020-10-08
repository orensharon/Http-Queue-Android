package com.orensharon.brainq.presentation.view;

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
import com.orensharon.BrainQ;
import com.orensharon.brainq.App;
import com.orensharon.brainq.R;
import com.orensharon.brainq.databinding.ActivityVisualizationBinding;
import com.orensharon.brainq.mock.Util;
import com.orensharon.brainq.presentation.model.GraphTime;
import com.orensharon.brainq.presentation.model.RequestEvent;
import com.orensharon.brainq.presentation.vm.VisualizationVM;
import com.orensharon.brainq.presentation.vm.VisualizationViewModelFactory;
import com.orensharon.brainq.service.HTTPMethods;
import com.orensharon.brainq.service.HttpQueueIntentService;
import com.orensharon.brainq.presentation.util.DateUtil;

import java.util.Date;

import javax.inject.Inject;


public class VisualizationActivity extends AppCompatActivity {

    private final static String TAG = VisualizationActivity.class.getSimpleName();

    private LineGraphSeries<DataPoint> successSeries;
    private LineGraphSeries<DataPoint> failedSeries;
    private DateAsXAxisLabelFormatter hourlyFormat, dailyFormat, weeklyFormat;

    private ActivityVisualizationBinding binding;
    private VisualizationVM viewModel;

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
        this.viewModel.getGraphTime().observe(this, this::applyTimeScale);
    }

    private void appendSuccessEvent(RequestEvent event) {
        this.appendNewEvent(this.successSeries, event);
    }

    private void appendFailedEvent(RequestEvent event) {
        this.appendNewEvent(this.failedSeries, event);
    }

    private void appendNewEvent(LineGraphSeries<DataPoint> series, RequestEvent event) {
        Log.i(TAG, "appendNewEvent:" + event.toString());
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

    private void applyTimeScale(GraphTime graphTime) {
        LabelFormatter labelFormatter = this.getLabelFormatter(graphTime.getTimeScale());
        this.createGraphView(graphTime.getStart(), graphTime.getEnd(), labelFormatter);
    }

    private LabelFormatter getLabelFormatter(int timeScale) {
        LabelFormatter labelFormatter = null;
        switch (timeScale) {
            case BrainQ.TimeScale.HOURLY:
                labelFormatter = this.hourlyFormat;
                break;
            case BrainQ.TimeScale.DAILY:
                labelFormatter = this.dailyFormat;
                break;
            case BrainQ.TimeScale.WEEKLY:
                labelFormatter = this.weeklyFormat;
                break;
        }
        return labelFormatter;
    }

    private void createGraphView(long start, long end, LabelFormatter labelFormatter) {
        this.binding.graphContainer.removeAllViews();
        GraphView graphView = new GraphView(this);
        graphView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        graphView.addSeries(this.successSeries);
        graphView.addSeries(this.failedSeries);
        graphView.getViewport().setMinX(start);
        graphView.getViewport().setMaxX(end);
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getGridLabelRenderer().setHumanRounding(false);
        //graphView.getGridLabelRenderer().setNumHorizontalLabels(5);
        graphView.getGridLabelRenderer().setLabelFormatter(labelFormatter);
        this.binding.graphContainer.addView(graphView);
    }

    private void initGraphComponents() {
        // Init x axis label formatter
        this.hourlyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getHourlyDateInFormat());
        this.dailyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getDailyDateInFormat());
        this.weeklyFormat = new DateAsXAxisLabelFormatter(this, DateUtil.getWeeklyDateInFormat());

        // Series
        this.successSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(this.viewModel.getStart(), 0)
        });
        this.failedSeries = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(this.viewModel.getStart(), 0)
        });
        this.successSeries.setColor(Color.GREEN);
        this.failedSeries.setColor(Color.RED);
    }
}