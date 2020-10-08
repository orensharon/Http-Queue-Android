package com.orensharon.httpqueue.data.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {RequestEntity.class}, version = 1)
public abstract class RequestDatabase extends RoomDatabase {
    public abstract RequestDAO getRequestDAO();
}
