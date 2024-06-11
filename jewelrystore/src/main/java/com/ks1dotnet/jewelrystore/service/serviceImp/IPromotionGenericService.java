package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IPromotionGenericService<T> {
    ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO);

    ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO);

    ResponseData checkInOtherActivePromotions(int entityId, int promotionId);

    ResponseData getEntitiesNotInPromotion(int promotionId);

    ResponseData getEntitiesInPromotion(int promotionId);
}