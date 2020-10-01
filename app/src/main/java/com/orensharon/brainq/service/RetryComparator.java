package com.orensharon.brainq.service;

import com.orensharon.brainq.Request;

import java.util.Comparator;

public class RetryComparator implements Comparator<Request> {
    @Override
    public int compare(Request x, Request y) {
        return Integer.compare(x.getRetries(), y.getRetries());
    }
}
