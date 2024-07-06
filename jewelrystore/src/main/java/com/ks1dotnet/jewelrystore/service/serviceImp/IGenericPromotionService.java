package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.Enum.EntityType;
import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IGenericPromotionService {
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData checkEntityInOtherPromotions(EntityType entityType, int entityId,
            int promotionId);

    public ResponseData getEntitiesNotInPromotion(EntityType entityType, int promotionId);

    public ResponseData getEntitiesInPromotion(EntityType entityType, int promotionId);
}
