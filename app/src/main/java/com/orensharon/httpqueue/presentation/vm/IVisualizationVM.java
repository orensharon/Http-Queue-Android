package com.orensharon.httpqueue.presentation.vm;

import androidx.lifecycle.LiveData;

import com.orensharon.httpqueue.presentation.model.RequestEvent;

import java.util.List;

public interface IVisualizationVM {
    void init();
    LiveData<Integer> getTimeScale();
    LiveData<Boolean> getValidClick();
    LiveData<Boolean> getInvalidClick();
    LiveData<RequestEvent> getLastSuccessEvent();
    LiveData<RequestEvent> getLastFailedEvent();
    long getStartTime();
    long getEndTime();
    List<RequestEvent> getAllSuccessEvents();
    List<RequestEvent> getAllFailedEvents();
}
