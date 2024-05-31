package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IForProductService {
    public ResponseData getProductsByPromotionId(int promotionId);

    public ResponseData getProductsNotInPromotion(int promotionId);

    public ResponseData applyPromotionToProducts(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData removePromotionFromProducts(ApplyPromotionDTO applyPromotionDTO);
}
