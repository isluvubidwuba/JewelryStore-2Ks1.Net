package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.Map;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IProductService extends ICRUDService<ProductDTO, Integer> {
        public ResponseData updateStatus(int id, int status);

        public ResponseData updateStatus(Map<Integer, Integer> list);

        // public ResponseData searchProduct(String search, String id_material, String
        // id_product_category,
        // String id_counter);

        public ResponseData searchProductV2(String search, String id_material,
                        String id_product_category, String id_counter);

        public ResponseData getProductByBarCode(String barCode);
}
