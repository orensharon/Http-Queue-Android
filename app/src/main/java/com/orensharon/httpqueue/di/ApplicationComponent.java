package com.orensharon.httpqueue.di;

import com.orensharon.httpqueue.App;
import com.orensharon.httpqueue.presentation.view.VisualizationActivity;
import com.orensharon.httpqueue.service.HttpQueueIntentService;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class
})
public interface ApplicationComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(App application);
        ApplicationComponent build();
    }
    void inject(App app);
    void inject(VisualizationActivity visualizationActivity);
    void inject(HttpQueueIntentService service);
}
