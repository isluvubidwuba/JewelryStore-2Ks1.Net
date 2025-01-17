package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface ICounterSerivce {
    public ResponseData insert(String name);

    public ResponseData addProductsToCounter(int counterId, List<ProductDTO> products);

    public ResponseData listProductsByCounter(int counterId, int page);

    public ResponseData getAllProductsInCounterOne();

    public ResponseData moveProductsToCounter(List<Integer> productIds, int newCounterId);

    public ResponseData getProductDetails(int productId);

    public ResponseData getAllProducts();

    public ResponseData getAllCountersActive();

    public ResponseData deleteCounter(int counterId);

    public ResponseData getInactiveCounters();

    public ResponseData updateCounter(int id, String name, boolean status);
}
