package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IForProductTypeService {
    public ResponseData applyCategoriesToVoucherType(ApplyPromotionDTO applyCategoriesDTO);

    public ResponseData removeCategoriesFromVoucherType(ApplyPromotionDTO applyCategoriesDTO);

    public ResponseData getCategoriesNotInVoucherType(int voucherTypeId);
}
