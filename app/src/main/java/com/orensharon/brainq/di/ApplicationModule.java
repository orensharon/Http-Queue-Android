package com.orensharon.brainq.di;

import android.content.Context;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orensharon.brainq.App;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.service.QueueManager;
import com.orensharon.brainq.service.RequestDispatcher;
import com.orensharon.brainq.service.RequestService;

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
    RequestService provideRequestService(RequestRepository repository, RequestDispatcher dispatcher, QueueManager queueManager) {
        return new RequestService(repository, queueManager, dispatcher);
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

}
