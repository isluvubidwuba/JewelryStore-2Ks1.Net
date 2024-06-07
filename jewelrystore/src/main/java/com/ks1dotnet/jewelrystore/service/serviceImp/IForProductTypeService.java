package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IForProductTypeService {
    public ResponseData getCategoriesByPromotionId(int promotionId);

    public ResponseData applyPromotionToCategories(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData removePromotionFromCategories(ApplyPromotionDTO applyPromotionDTO);

    public ResponseData checkCategoryInOtherActivePromotions(int categoryId, int currentPromotionId);

    public ResponseData getCategoriesNotInPromotion(int promotionId);
}
