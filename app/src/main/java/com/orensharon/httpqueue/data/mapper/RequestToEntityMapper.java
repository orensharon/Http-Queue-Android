package com.orensharon.httpqueue.data.mapper;

import com.orensharon.httpqueue.IMapper;
import com.orensharon.httpqueue.data.model.Request;
import com.orensharon.httpqueue.data.room.RequestEntity;

import java.util.Date;

public class RequestToEntityMapper implements IMapper<Request, RequestEntity> {
    @Override
    public RequestEntity map(Request from) {
        return new RequestEntity(
                from.getId(),
                from.getEndpoint(),
                from.getMethod(),
                from.getPayload(),
                from.getReties(),
                new Date(from.getLastRetryMs())
        );
    }
}
