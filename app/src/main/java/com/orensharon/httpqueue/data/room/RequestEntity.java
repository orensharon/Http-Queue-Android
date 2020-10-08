package com.orensharon.httpqueue.data.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.orensharon.httpqueue.data.room.util.DateConverter;

import java.util.Date;

@Entity
@TypeConverters(DateConverter.class)
public class RequestEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    String endPoint;

    int method;

    String payload;

    int retries;

    @ColumnInfo(name = "last_retry_timestamp")
    Date ts;

    public RequestEntity(long id, String endPoint, int method, String payload, int retries, Date ts) {
        this.id = id;
        this.method = method;
        this.endPoint = endPoint;
        this.payload = payload;
        this.retries = retries;
        this.ts = ts;
    }

    @Ignore
    public RequestEntity(String endPoint, int method, String payload, int retries, Date ts) { ;
        this.method = method;
        this.endPoint = endPoint;
        this.payload = payload;
        this.retries = retries;
        this.ts = ts;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public int getMethod() {
        return method;
    }

    public String getPayload() {
        return payload;
    }

    public int getRetries() {
        return retries;
    }

    public long getTs() {
        return ts.getTime();
    }
}
