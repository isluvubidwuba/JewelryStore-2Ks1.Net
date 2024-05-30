package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IGemStoneOfProductService extends ICRUDService<GemStoneOfProductDTO, Integer> {
    public ResponseData getGemStonesByProductId(int id);
}
