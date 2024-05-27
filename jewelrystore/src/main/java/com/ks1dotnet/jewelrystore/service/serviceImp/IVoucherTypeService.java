package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.entity.VoucherType;

public interface IVoucherTypeService {
    public VoucherType getVoucherById(int id);

    public List<VoucherType> findAll();
}
