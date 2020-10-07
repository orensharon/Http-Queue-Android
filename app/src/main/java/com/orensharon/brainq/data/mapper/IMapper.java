package com.orensharon.brainq.data.mapper;

public interface IMapper<F, T> {
    T map(F from);
}
