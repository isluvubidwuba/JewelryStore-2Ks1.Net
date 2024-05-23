package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface ICRUDService<T, Id> {
    public ResponseData findAll();

    public ResponseData findById(Id id);

    public ResponseData insert(T t);

    public ResponseData update(T t);

    public ResponseData existsById(Id id);
}
