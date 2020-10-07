package com.orensharon.brainq.data.room;

import androidx.room.Dao;
import androidx.room.Query;

import com.orensharon.brainq.data.room.util.BaseDao;

import java.util.List;

@Dao
public abstract class RequestDAO extends BaseDao<RequestEntity> {

    @Query("SELECT * FROM RequestEntity")
    abstract List<RequestEntity> getRequests();

}