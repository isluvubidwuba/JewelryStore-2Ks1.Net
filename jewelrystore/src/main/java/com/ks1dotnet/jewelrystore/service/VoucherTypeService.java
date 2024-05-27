package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.VoucherType;
import com.ks1dotnet.jewelrystore.repository.IVoucherTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IVoucherTypeService;

@Service
public class VoucherTypeService implements IVoucherTypeService {

    @Autowired
    private IVoucherTypeRepository iVoucherTypeRepository;

    @Override
    public VoucherType getVoucherById(int id) {
        return iVoucherTypeRepository.findById(id).orElse(null);
    }

    @Override
    public List<VoucherType> findAll() {
        return iVoucherTypeRepository.findAll();
    }

}
