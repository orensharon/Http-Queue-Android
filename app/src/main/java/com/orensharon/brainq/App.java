package com.orensharon.brainq;

import android.app.Application;

import com.orensharon.brainq.di.ApplicationComponent;
import com.orensharon.brainq.di.DaggerApplicationComponent;

public class App extends Application {

    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        this.applicationComponent = DaggerApplicationComponent
                .builder()
                .application(this)
                .build();
        this.applicationComponent.inject(this);
    }
}
