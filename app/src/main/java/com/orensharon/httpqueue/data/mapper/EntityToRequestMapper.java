package com.orensharon.httpqueue.data.mapper;

import com.orensharon.httpqueue.IMapper;
import com.orensharon.httpqueue.data.model.Request;
import com.orensharon.httpqueue.data.room.RequestEntity;

public class EntityToRequestMapper implements IMapper<RequestEntity, Request> {
    @Override
    public Request map(RequestEntity from) {
        return new Request(
                from.getId(),
                from.getEndPoint(),
                from.getPayload(),
                from.getMethod(),
                from.getRetries(),
                from.getTs()
        );
    }
}
