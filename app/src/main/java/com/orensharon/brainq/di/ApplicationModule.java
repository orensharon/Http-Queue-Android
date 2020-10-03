package com.orensharon.brainq.di;

import android.content.Context;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orensharon.brainq.App;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.service.QueueManager;
import com.orensharon.brainq.service.RequestDispatcher;
import com.orensharon.brainq.service.RequestService;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @Provides
    Context provideContext(App application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    RequestRepository provideRequestRepository() {
        return new RequestRepository();
    }

    @Singleton
    @Provides
    RequestService provideRequestService(RequestRepository repository, RequestDispatcher dispatcher, QueueManager queueManager, EventBus eventBus) {
        return new RequestService(repository, queueManager, dispatcher, eventBus);
    }

    @Singleton
    @Provides
    RequestDispatcher provideRequestDispatcher(RequestQueue requestQueue) {
        return new RequestDispatcher(requestQueue);
    }

    @Singleton
    @Provides
    RequestQueue provideRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

    @Singleton
    @Provides
    QueueManager provideQueueManager() {
        return new QueueManager();
    }

    @Singleton
    @Provides
    EventBus provideEventBus() {
        return EventBus.builder()
                .logNoSubscriberMessages(false)
                .logSubscriberExceptions(true)
                .installDefaultEventBus();
    }
}
