package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IForCustomerService {
    public ResponseData getCustomerTypesByPromotionId(int promotionId);

    public ResponseData applyPromotionToCustomerTypes(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData removePromotionFromCustomerTypes(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData checkCustomerTypeInOtherActivePromotions(int customerTypeId, int currentPromotionId);

    public ResponseData getCustomerTypesNotInPromotion(int promotionId);
}
