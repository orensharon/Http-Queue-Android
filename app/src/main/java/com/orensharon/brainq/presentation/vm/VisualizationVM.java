package com.orensharon.brainq.presentation.vm;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orensharon.BrainQ;
import com.orensharon.brainq.data.event.RequestStateChangedEvent;
import com.orensharon.brainq.presentation.model.RequestEvent;
import com.orensharon.brainq.presentation.model.Visualization;
import com.orensharon.brainq.presentation.util.SingleLiveEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VisualizationVM extends ViewModel {

    private final static String TAG = VisualizationVM.class.getSimpleName();

    private Visualization visualizationModel;

    private final MutableLiveData<Integer> timeScale;
    private final SingleLiveEvent<Boolean> validClick;
    private final SingleLiveEvent<Boolean> invalidClick;
    private final SingleLiveEvent<RequestEvent> successEvent;
    private final SingleLiveEvent<RequestEvent> failedEvent;

    private final EventBus eventBus;

    public VisualizationVM(EventBus eventBus) {
        this.eventBus = eventBus;
        this.visualizationModel = new Visualization();
        this.timeScale = new MutableLiveData<>();
        this.validClick = new SingleLiveEvent<>();
        this.invalidClick = new SingleLiveEvent<>();
        this.successEvent = new SingleLiveEvent<>();
        this.failedEvent = new SingleLiveEvent<>();
        this.timeScale.setValue(BrainQ.TimeScale.HOURLY);
    }

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

    public MutableLiveData<Integer> getTimeScale() {
        return this.timeScale;
    }

    public LiveData<Boolean> getValidClick() {
        return validClick;
    }

    public LiveData<Boolean> getInvalidClick() {
        return invalidClick;
    }

    public LiveData<RequestEvent> getLastSuccessEvent() {
        return successEvent;
    }

    public LiveData<RequestEvent> getLastFailedEvent() {
        return failedEvent;
    }

    public void onValidClicked() {
        this.validClick.setValue(true);
    }

    public void onInvalidClicked() {
        this.invalidClick.setValue(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRequestStateChangedEvent(RequestStateChangedEvent event) {
        Log.i(TAG, "onRequestStateChangedEvent " + event.toString());
        RequestEvent requestEvent = this.visualizationModel.add(event.requestId, event.state, event.ts);
        if (event.state) {
            this.successEvent.setValue(requestEvent);
            return;
        }
        this.failedEvent.setValue(requestEvent);
    }
}
