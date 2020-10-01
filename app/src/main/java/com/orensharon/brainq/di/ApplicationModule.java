package com.orensharon.brainq.di;

import android.content.Context;


import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orensharon.brainq.App;
import com.orensharon.brainq.data.RequestRepository;
import com.orensharon.brainq.service.QueueManager;

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
    RequestRepository provideArtistRepository() {
        return new RequestRepository();
    }

    @Singleton
    @Provides
    QueueManager provideQueueManager(RequestQueue requestQueue, RequestRepository requestRepository) {
        return new QueueManager(requestQueue, requestRepository);
    }

    @Singleton
    @Provides
    RequestQueue provideRequestQueue(Context context) {
        return Volley.newRequestQueue(context);
    }

}
