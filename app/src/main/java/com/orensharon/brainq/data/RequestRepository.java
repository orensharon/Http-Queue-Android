package com.orensharon.brainq.data;

import com.orensharon.brainq.data.mapper.EntityToRequestMapper;
import com.orensharon.brainq.data.mapper.RequestToEntityMapper;
import com.orensharon.brainq.data.model.Request;
import com.orensharon.brainq.data.room.RequestDAO;
import com.orensharon.brainq.data.room.RequestEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRepository {

    private final Map<Long, Request> requests;
    private final RequestDAO local;

    public RequestRepository(RequestDAO dao) {
        this.requests = new ConcurrentHashMap<>();
        this.local = dao;
    }

    public void init() {
        List<RequestEntity> entities = this.local.list();
        for (RequestEntity entity : entities) {
            this.requests.put(entity.getId(), new EntityToRequestMapper().map(entity));
        }
    }

    public Request getById(long requestId) {
        Request request =  this.requests.get(requestId);
        if (request == null) {
            return null;
        }
        return new Request(request);
    }

    public List<Request> list() {
        List<Request> result = new ArrayList<>();
        for (Request request : this.requests.values()) {
            result.add(new Request(request));
        }
        return result;
    }

    public void store(Request request) {
        long id = this.local.upsert(new RequestToEntityMapper().map(request));
        if (id > 0) {
            // New id generated to request - update domain entity
            // Update request id after inserted into local db
            request.setId(id);
        }
        this.requests.put(request.getId(), new Request(request));
    }
}
