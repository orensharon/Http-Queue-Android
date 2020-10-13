package com.orensharon.httpqueue.di;

import com.orensharon.httpqueue.presentation.view.VisualizationActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ViewBuildersModule {

    @ContributesAndroidInjector()
    abstract VisualizationActivity bindVisualizationActivity();
}
