package com.orensharon.httpqueue;

import android.app.Application;

import com.orensharon.httpqueue.di.DaggerApplicationComponent;
import com.orensharon.httpqueue.service.RequestService;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class App extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> dispatchingAndroidInjector;

    @Inject
    RequestService requestService;

    @Override
    public void onCreate() {
        super.onCreate();
        DaggerApplicationComponent
                .builder()
                .application(this)
                .build()
                .inject(this);
        this.requestService.init();
        this.requestService.start();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return this.dispatchingAndroidInjector;
    }
}
