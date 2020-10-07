package com.orensharon.brainq.data.mapper;

import com.orensharon.brainq.data.Request;
import com.orensharon.brainq.data.room.RequestEntity;

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
