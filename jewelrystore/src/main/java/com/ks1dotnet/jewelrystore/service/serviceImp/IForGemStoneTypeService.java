package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IForGemStoneTypeService {
    public ResponseData getGemStoneTypesByPromotionId(int promotionId);

    public ResponseData applyPromotionToGemStoneTypes(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData removePromotionFromGemStoneTypes(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData checkGemStoneTypeInOtherActivePromotions(int gemstoneTypeId, int currentPromotionId);

    public ResponseData getGemStoneTypesNotInPromotion(int promotionId);
}
