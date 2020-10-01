package com.orensharon.brainq;

import android.app.Application;

import com.orensharon.brainq.di.ApplicationComponent;
import com.orensharon.brainq.di.DaggerApplicationComponent;
import com.orensharon.brainq.service.HttpRequestQueue;

import javax.inject.Inject;

public class App extends Application {

    public ApplicationComponent applicationComponent;

    @Inject
    HttpRequestQueue httpRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        this.applicationComponent = DaggerApplicationComponent
                .builder()
                .application(this)
                .build();
        this.applicationComponent.inject(this);

        this.httpRequestQueue.listen();
    }
}
