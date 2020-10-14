package com.orensharon.httpqueue.presentation.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orensharon.httpqueue.data.event.RequestStateChangedEvent;
import com.orensharon.httpqueue.presentation.TimeScale;
import com.orensharon.httpqueue.presentation.model.RequestEvent;
import com.orensharon.httpqueue.presentation.model.Visualization;
import com.orensharon.httpqueue.presentation.util.SingleLiveEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

// TODO: get list of all existing request - using rx?
public class VisualizationVM extends ViewModel implements IVisualizationVM {

    private final static String TAG = VisualizationVM.class.getSimpleName();

    private Visualization visualization;

    private final MutableLiveData<Integer> timeScale;
    private final MutableLiveData<Integer> successPercentage;
    private final SingleLiveEvent<Boolean> validClick;
    private final SingleLiveEvent<Boolean> invalidClick;
    private final SingleLiveEvent<RequestEvent> successEvent;
    private final SingleLiveEvent<RequestEvent> failedEvent;

    private final EventBus eventBus;

    public VisualizationVM(EventBus eventBus) {
        this.eventBus = eventBus;
        this.visualization = new Visualization(TimeScale.MINUTELY);
        this.timeScale = new MutableLiveData<>();
        this.successPercentage = new MutableLiveData<>();
        this.validClick = new SingleLiveEvent<>();
        this.invalidClick = new SingleLiveEvent<>();
        this.successEvent = new SingleLiveEvent<>();
        this.failedEvent = new SingleLiveEvent<>();
        // TODO: here?
        this.timeScale.setValue(this.visualization.getTimeScale());
        this.successPercentage.setValue(this.visualization.getSuccessPercentage());
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
        Log.d(TAG, "onCleared");
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
        return this.visualization.getAllSuccessEventsImmutable();
    }

    @Override
    public List<RequestEvent> getAllFailedEvents() {
        return this.visualization.getAllFailedEventsImmutable();
    }

    public void changeTimeScale(int timeScale) {
        this.visualization.changeTimeScale(timeScale);
        this.timeScale.setValue(this.visualization.getTimeScale());
        this.successPercentage.setValue(this.visualization.getSuccessPercentage());
    }

    public void onValidClicked() {
        this.validClick.setValue(true);
    }

    public void onInvalidClicked() {
        this.invalidClick.setValue(true);
    }

    public LiveData<Integer> getSuccessPercentage() {
        return successPercentage;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestStateChangedEvent(RequestStateChangedEvent event) {
        Log.d(TAG, "onRequestStateChangedEvent " + event.toString());
        RequestEvent requestEvent = this.visualization.add(event.requestId, event.success, event.ts);
        int percentage = this.visualization.getSuccessPercentage();
        if (event.success) {
            this.successEvent.setValue(requestEvent);
        } else {
            this.failedEvent.setValue(requestEvent);
        }
        this.successPercentage.setValue(percentage);
    }
}
