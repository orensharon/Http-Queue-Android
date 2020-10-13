package com.orensharon.httpqueue.di;

import com.orensharon.httpqueue.service.HttpQueueIntentService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AndroidServiceBuildersModule {
    @ContributesAndroidInjector
    abstract HttpQueueIntentService provideHttpQueueIntentService();
}
