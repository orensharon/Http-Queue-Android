package com.orensharon.brainq.presentation.vm;

import androidx.lifecycle.LiveData;

import com.orensharon.brainq.presentation.model.RequestEvent;

import java.util.List;

public interface IVisualizationVM {
    void init();
    LiveData<Integer> getTimeScale();
    LiveData<Boolean> getValidClick();
    LiveData<Boolean> getInvalidClick();
    LiveData<RequestEvent> getLastSuccessEvent();
    LiveData<RequestEvent> getLastFailedEvent();
    // TODO: using getter this way is good practice?
    long getStartTime();
    long getEndTime();
    List<RequestEvent> getAllSuccessEvents();
    List<RequestEvent> getAllFailedEvents();
    // ***
}
