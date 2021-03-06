package com.orensharon.httpqueue.data;

import com.orensharon.httpqueue.data.mapper.EntityToRequestMapper;
import com.orensharon.httpqueue.data.mapper.RequestToEntityMapper;
import com.orensharon.httpqueue.data.model.Request;
import com.orensharon.httpqueue.data.room.RequestDAO;
import com.orensharon.httpqueue.data.room.RequestEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRepository {

    // Save cache of all request to reduce dao operations
    // in case of many requests
    private final Map<Long, Request> requests;
    private final RequestDAO local;

    public RequestRepository(RequestDAO dao) {
        this.requests = new ConcurrentHashMap<>();
        this.local = dao;
    }

    public void init() {
        // Map all existing entities to domain model
        List<RequestEntity> entities = this.local.listAllFails();
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
        // TODO: as sorted list
        List<Request> result = new ArrayList<>();
        for (Request request : this.requests.values()) {
            result.add(new Request(request));
        }
        return result;
    }

    public void store(Request request) {
        // Insert if not exists, update if exists
        long id = this.local.upsert(new RequestToEntityMapper().map(request));
        if (id > 0) {
            // New id generated to request - update domain entity
            // Update request id after inserted into local db
            request.setId(id);
        }
        this.requests.put(request.getId(), new Request(request));
    }
}
