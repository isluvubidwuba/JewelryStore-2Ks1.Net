package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.responseData;

public interface ICRUDService<T, Id> {
    public responseData findAll();

    public responseData findById(Id id);

    public responseData save(T t);

    public responseData saveAndFlush(T t);

    public responseData saveAll(Iterable<T> t);

    public responseData saveAllAndFlush(Iterable<T> t);

    public responseData existsById(Id id);

    public responseData deleteById(Id id);
}
