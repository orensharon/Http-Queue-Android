package com.orensharon.brainq.di;

import com.orensharon.brainq.App;
import com.orensharon.brainq.service.HttpQueueService;

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
    void inject(HttpQueueService service);
}
