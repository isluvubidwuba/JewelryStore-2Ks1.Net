package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;

public interface IGemStoneOfProductService extends ICRUDService<GemStoneOfProductDTO, Integer> {
    public GemStoneOfProductDTO update(int id, String color, String clarity, float carat,
            double price, int id_gemStoneType, int id_gemStone_category, String id_product);

    public GemStoneOfProductDTO insert(String color, String clarity, float carat, double price,
            int id_gemStoneType, int id_gemStone_category, String id_product);
}
