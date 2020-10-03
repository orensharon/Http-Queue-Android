package com.orensharon.brainq.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestRepository {

    private final Map<Integer, Request> requests;

    public RequestRepository() {
        this.requests = new ConcurrentHashMap<>();
    }

    public void add(Request request) {
        this.requests.put(request.getId(), new Request(request));
    }

    public Request getById(int requestId) {
        Request request =  this.requests.get(requestId);
        if (request == null) {
            return null;
        }
        return new Request(request);
    }

    public void save(Request request) {
        this.requests.put(request.getId(), new Request(request));
    }
}
