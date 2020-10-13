package com.orensharon.httpqueue.di;

import com.orensharon.httpqueue.App;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        AndroidSupportInjectionModule.class,
        ViewBuildersModule.class,
        AndroidServiceBuildersModule.class
})
public interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);
        ApplicationComponent build();
    }
    void inject(App app);
}
