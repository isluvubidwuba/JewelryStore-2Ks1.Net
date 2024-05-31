package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.VoucherTypeDTO;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.entity.VoucherType;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.repository.IForProductTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IVoucherTypeService;

@Service
public class VoucherTypeService implements IVoucherTypeService {

    @Autowired
    private IVoucherTypeRepository iVoucherTypeRepository;

    @Autowired
    private IForProductTypeRepository iForProductTypeRepository;

    @Override
    public VoucherType getVoucherById(int id) {
        return iVoucherTypeRepository.findById(id).orElse(null);
    }

    @Override
    public List<VoucherType> findAll() {
        return iVoucherTypeRepository.findAll();
    }

    @Override
    public VoucherType getVoucherTypeById(Integer id) {
        try {
            return iVoucherTypeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("VoucherType not found with id: " + id));
        } catch (Exception ex) {
            throw new ResourceNotFoundException("VoucherType not found with id: " + id);
        }
    }

    @Override
    public VoucherType createVoucherType(VoucherType voucherType) {
        try {
            return iVoucherTypeRepository.save(voucherType);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid input data for creating voucher type");
        }
    }

    @Override
    public VoucherTypeDTO updateVoucherType(int id, String type) {
        try {
            VoucherType voucherType = getVoucherById(id);
            voucherType.setType(type);
            return iVoucherTypeRepository.save(voucherType).getDTO();
        } catch (Exception ex) {
            throw new BadRequestException("Invalid input data for updating voucher type");
        }
    }

    @Override
    public List<ProductCategoryDTO> getCategoriesByVoucherTypeId(Integer id) {
        try {
            List<ProductCategory> categories = iForProductTypeRepository.findCategoriesByVoucherTypeId(id);
            return categories.stream()
                    .map(ProductCategory::getDTO)
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            throw new ResourceNotFoundException("VoucherType not found with id: " + id);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        return iVoucherTypeRepository.existsById(id);
    }

}
