package com.orensharon.brainq.data.mapper;

import com.orensharon.brainq.IMapper;
import com.orensharon.brainq.data.model.Request;
import com.orensharon.brainq.data.room.RequestEntity;

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
