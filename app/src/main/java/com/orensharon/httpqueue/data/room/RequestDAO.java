package com.orensharon.httpqueue.data.room;

import androidx.room.Dao;
import androidx.room.Query;

import com.orensharon.httpqueue.data.room.util.BaseDao;

import java.util.List;

@Dao
public abstract class RequestDAO extends BaseDao<RequestEntity> {

    @Query("SELECT * FROM RequestEntity")
    public abstract List<RequestEntity> list();

    @Query("SELECT * FROM RequestEntity WHERE retries > 0")
    public abstract List<RequestEntity> listAllFails();
}