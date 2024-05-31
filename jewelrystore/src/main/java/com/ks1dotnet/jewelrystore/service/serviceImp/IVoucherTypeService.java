package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.VoucherTypeDTO;
import com.ks1dotnet.jewelrystore.entity.VoucherType;

public interface IVoucherTypeService {
    public VoucherType getVoucherById(int id);

    public List<VoucherType> findAll();

    public VoucherType getVoucherTypeById(Integer id);

    public VoucherType createVoucherType(VoucherType voucherType);

    public VoucherTypeDTO updateVoucherType(int id, String type);

    public List<ProductCategoryDTO> getCategoriesByVoucherTypeId(Integer id);

    public boolean existsById(Integer id);

}
