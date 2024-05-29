package com.modules.base.domain;

public interface Repository<T extends AggregateRoot<T, TID>, TID> {
    void add(T entity);
    T find(TID id);
}
