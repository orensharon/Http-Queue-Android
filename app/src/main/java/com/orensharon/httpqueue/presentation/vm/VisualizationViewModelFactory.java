package com.orensharon.httpqueue.presentation.vm;


import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class VisualizationViewModelFactory implements ViewModelProvider.Factory {

    private final EventBus eventBus;

    @Inject
    public VisualizationViewModelFactory(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VisualizationVM.class)) {
            return (T) new VisualizationVM(this.eventBus);
        }
        throw new IllegalArgumentException("Unknown class name");
    }
}
