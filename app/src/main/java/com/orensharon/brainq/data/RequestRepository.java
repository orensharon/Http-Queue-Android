package com.orensharon.brainq.data;

import java.util.HashMap;
import java.util.Map;

public class RequestRepository {

    private final Map<Integer, Request> requests;

    public RequestRepository() {
        this.requests = new HashMap<>();
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
