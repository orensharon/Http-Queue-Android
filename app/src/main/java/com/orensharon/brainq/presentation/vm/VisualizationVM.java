package com.orensharon.brainq.presentation.vm;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.orensharon.brainq.R;
import com.orensharon.brainq.util.SingleLiveEvent;

public class VisualizationVM extends ViewModel {

    private final MutableLiveData<Integer> timeScale;
    private final SingleLiveEvent<Integer> sendEvent;

    public VisualizationVM() {
        this.timeScale = new MutableLiveData<>();
        this.sendEvent = new SingleLiveEvent<>();
        this.timeScale.setValue(R.id.week);
    }

    public MutableLiveData<Integer> getTimeScale() {
        return this.timeScale;
    }

    public LiveData<Integer> getSendEvent() {
        return sendEvent;
    }

    public void addEvent(long requestId, boolean state, long ts) {

    }


    public void onValidClicked() {
        this.sendEvent.setValue(0);
    }

    public void onInvalidClicked() {
        this.sendEvent.setValue(1);
    }
}
