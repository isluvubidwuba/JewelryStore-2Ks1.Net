package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.Map;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IProductService extends ICRUDService<ProductDTO, String> {
    public ResponseData updateStatus(String id, int status);

    public ResponseData updateStatus(Map<String, Integer> list);

}
