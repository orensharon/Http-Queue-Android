package com.orensharon.brainq;

public interface IMapper<F, T> {
    T map(F from);
}
