package com.orensharon.brainq.presentation.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orensharon.brainq.data.event.RequestStateChangedEvent;
import com.orensharon.brainq.presentation.TimeScale;
import com.orensharon.brainq.presentation.model.GraphTime;
import com.orensharon.brainq.presentation.model.RequestEvent;
import com.orensharon.brainq.presentation.model.Visualization;
import com.orensharon.brainq.presentation.util.SingleLiveEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VisualizationVM extends ViewModel implements IVisualizationVM {

    private final static String TAG = VisualizationVM.class.getSimpleName();

    private Visualization visualizationModel;

    private final MutableLiveData<GraphTime> graphTime;
    private final MutableLiveData<Integer> ratio;
    private final SingleLiveEvent<Boolean> validClick;
    private final SingleLiveEvent<Boolean> invalidClick;
    private final SingleLiveEvent<RequestEvent> successEvent;
    private final SingleLiveEvent<RequestEvent> failedEvent;

    private final EventBus eventBus;

    public VisualizationVM(EventBus eventBus) {
        this.eventBus = eventBus;
        this.visualizationModel = new Visualization(TimeScale.HOURLY);
        this.graphTime = new MutableLiveData<>();
        this.ratio = new MutableLiveData<>();
        this.validClick = new SingleLiveEvent<>();
        this.invalidClick = new SingleLiveEvent<>();
        this.successEvent = new SingleLiveEvent<>();
        this.failedEvent = new SingleLiveEvent<>();
        // TODO: here?
        this.graphTime.setValue(this.visualizationModel.getGraphTime());
        this.ratio.setValue(this.visualizationModel.getSuccessRatio());
    }

    @Override
    public void init() {
        if (!this.eventBus.isRegistered(this)) {
            this.eventBus.register(this);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "onCleared");
        this.eventBus.unregister(this);
    }

    @Override
    public LiveData<GraphTime> getGraphTime() {
        return this.graphTime;
    }

    @Override
    public LiveData<Boolean> getValidClick() {
        return validClick;
    }

    @Override
    public LiveData<Boolean> getInvalidClick() {
        return invalidClick;
    }

    @Override
    public LiveData<RequestEvent> getLastSuccessEvent() {
        return successEvent;
    }

    @Override
    public LiveData<RequestEvent> getLastFailedEvent() {
        return failedEvent;
    }

    @Override
    public long getStart() {
        return this.visualizationModel.getGraphTime().getStart();
    }

    public void changeTimeScale(int timeScale) {
        this.visualizationModel.changeTimeScale(timeScale);
        this.graphTime.setValue(this.visualizationModel.getGraphTime());
    }
    public void onValidClicked() {
        this.validClick.setValue(true);
    }

    public void onInvalidClicked() {
        this.invalidClick.setValue(true);
    }

    public LiveData<Integer> getRatio() {
        return ratio;
    }

    public int getTimeScale() {
        return this.visualizationModel.getGraphTime().getTimeScale();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestStateChangedEvent(RequestStateChangedEvent event) {
        Log.i(TAG, "onRequestStateChangedEvent " + event.toString());
        RequestEvent requestEvent = this.visualizationModel.add(event.requestId, event.state, event.ts);
        if (event.state) {
            this.successEvent.setValue(requestEvent);
        } else {
            this.failedEvent.setValue(requestEvent);
        }
        int ratio = this.visualizationModel.getSuccessRatio();
        this.ratio.setValue(ratio);
    }
}
