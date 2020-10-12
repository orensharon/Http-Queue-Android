package com.orensharon.httpqueue.di;

import android.content.Context;


import androidx.room.Room;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.orensharon.httpqueue.App;
import com.orensharon.httpqueue.data.RequestRepository;
import com.orensharon.httpqueue.data.room.RequestDAO;
import com.orensharon.httpqueue.data.room.RequestDatabase;
import com.orensharon.httpqueue.presentation.vm.VisualizationViewModelFactory;
import com.orensharon.httpqueue.service.QueueWorker;
import com.orensharon.httpqueue.service.RequestDispatcher;
import com.orensharon.httpqueue.service.RequestService;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    @Provides
    Context provideContext(App application) {
        return application.getApplicationContext();
    }

    @Provides
    Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Singleton
    @Provides
    RequestRepository provideRequestRepository(RequestDAO dao) {
        return new RequestRepository(dao);
    }

    @Singleton
    @Provides
    RequestService provideRequestService(RequestRepository repository, RequestDispatcher dispatcher, QueueWorker queueWorker, EventBus eventBus, Executor executor) {
        return new RequestService(repository, queueWorker, dispatcher, eventBus, executor);
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
    QueueWorker provideQueueWorker() {
        return new QueueWorker();
    }

    @Singleton
    @Provides
    EventBus provideEventBus() {
        return EventBus.builder()
                .logNoSubscriberMessages(false)
                .logSubscriberExceptions(true)
                .installDefaultEventBus();
    }

    @Provides
    @Singleton
    RequestDatabase provideRequestDatabase(Context context) {
        return Room.databaseBuilder(context, RequestDatabase.class, "request_db").build();
    }

    @Singleton
    @Provides
    RequestDAO provideRequestDAO(RequestDatabase database) {
        return database.getRequestDAO();
    }
}
