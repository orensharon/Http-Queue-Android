package com.orensharon.httpqueue;

import android.app.Application;

import com.orensharon.httpqueue.di.ApplicationComponent;
import com.orensharon.httpqueue.di.DaggerApplicationComponent;
import com.orensharon.httpqueue.service.RequestService;

import javax.inject.Inject;

public class App extends Application {

    public ApplicationComponent applicationComponent;

    @Inject
    RequestService requestService;

    @Override
    public void onCreate() {
        super.onCreate();
        this.applicationComponent = DaggerApplicationComponent
                .builder()
                .application(this)
                .build();
        this.applicationComponent.inject(this);

        this.requestService.start();
    }
}
