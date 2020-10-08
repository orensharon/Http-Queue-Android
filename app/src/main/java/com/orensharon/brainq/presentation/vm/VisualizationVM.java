package com.orensharon.brainq.presentation.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orensharon.brainq.data.event.RequestStateChangedEvent;
import com.orensharon.brainq.presentation.TimeScale;
import com.orensharon.brainq.presentation.model.RequestEvent;
import com.orensharon.brainq.presentation.model.Visualization;
import com.orensharon.brainq.presentation.util.SingleLiveEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class VisualizationVM extends ViewModel implements IVisualizationVM {

    private final static String TAG = VisualizationVM.class.getSimpleName();

    private Visualization visualization;

    private final MutableLiveData<Integer> timeScale;
    private final MutableLiveData<Integer> ratio;
    private final SingleLiveEvent<Boolean> validClick;
    private final SingleLiveEvent<Boolean> invalidClick;
    private final SingleLiveEvent<RequestEvent> successEvent;
    private final SingleLiveEvent<RequestEvent> failedEvent;

    private final EventBus eventBus;

    public VisualizationVM(EventBus eventBus) {
        this.eventBus = eventBus;
        this.visualization = new Visualization(TimeScale.MINUTELY);
        this.timeScale = new MutableLiveData<>();
        this.ratio = new MutableLiveData<>();
        this.validClick = new SingleLiveEvent<>();
        this.invalidClick = new SingleLiveEvent<>();
        this.successEvent = new SingleLiveEvent<>();
        this.failedEvent = new SingleLiveEvent<>();
        // TODO: here?
        this.timeScale.setValue(this.visualization.getTimeScale());
        this.ratio.setValue(this.visualization.getSuccessRatio());
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
    public LiveData<Integer> getTimeScale() {
        return this.timeScale;
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
    public long getStartTime() {
        return this.visualization.getStartTime();
    }

    @Override
    public long getEndTime() {
        return this.visualization.getEndTime();
    }

    @Override
    public List<RequestEvent> getAllSuccessEvents() {
        return this.visualization.getAllSuccessEvents();
    }

    @Override
    public List<RequestEvent> getAllFailedEvents() {
        return this.visualization.getAllFailedEvents();
    }

    public void changeTimeScale(int timeScale) {
        this.visualization.changeTimeScale(timeScale);
        this.timeScale.setValue(this.visualization.getTimeScale());
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestStateChangedEvent(RequestStateChangedEvent event) {
        Log.i(TAG, "onRequestStateChangedEvent " + event.toString());
        RequestEvent requestEvent = this.visualization.add(event.requestId, event.state, event.ts);
        if (event.state) {
            this.successEvent.setValue(requestEvent);
        } else {
            this.failedEvent.setValue(requestEvent);
        }
        int ratio = this.visualization.getSuccessRatio();
        this.ratio.setValue(ratio);
    }
}
