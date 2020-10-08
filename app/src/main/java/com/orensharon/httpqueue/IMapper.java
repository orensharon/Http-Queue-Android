package com.orensharon.httpqueue;

public interface IMapper<F, T> {
    T map(F from);
}
