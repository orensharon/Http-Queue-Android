package com.orensharon.brainq.presentation.vm;

import androidx.lifecycle.LiveData;

import com.orensharon.brainq.presentation.model.GraphTime;
import com.orensharon.brainq.presentation.model.RequestEvent;

public interface IVisualizationVM {
    void init();
    LiveData<GraphTime> getGraphTime();
    LiveData<Boolean> getValidClick();
    LiveData<Boolean> getInvalidClick();
    LiveData<RequestEvent> getLastSuccessEvent();
    LiveData<RequestEvent> getLastFailedEvent();
    long getStart();
}
