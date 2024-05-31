package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.entity.ForProductType;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.entity.VoucherType;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForProductTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IVoucherTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductTypeService;

@Service
public class ForProductTypeService implements IForProductTypeService {
    @Autowired
    IForProductTypeRepository iForProductTypeRepository;

    @Autowired
    IProductCategoryRepository iProductCategoryRepository;

    @Autowired
    IVoucherTypeRepository iVoucherTypeRepository;

    @Override
    public ResponseData applyCategoriesToVoucherType(ApplyPromotionDTO applyCategoriesDTO) {
        try {
            VoucherType voucherType = iVoucherTypeRepository.findById(applyCategoriesDTO.getPromotionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "VoucherType not found with id: " + applyCategoriesDTO.getPromotionId()));

            List<ProductCategory> categories = iProductCategoryRepository
                    .findAllById(applyCategoriesDTO.getProductIds());

            if (categories.isEmpty()) {
                throw new BadRequestException("No valid categories found for the given IDs");
            }

            List<ForProductType> forProductTypes = categories.stream()
                    .map(category -> new ForProductType(null, voucherType, category))
                    .collect(Collectors.toList());

            iForProductTypeRepository.saveAll(forProductTypes);

            return new ResponseData(HttpStatus.OK, "Categories applied to VoucherType successfully", null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply categories to VoucherType", e.getMessage());
        }
    }

    @Override
    public ResponseData removeCategoriesFromVoucherType(ApplyPromotionDTO applyCategoriesDTO) {
        try {
            VoucherType voucherType = iVoucherTypeRepository.findById(applyCategoriesDTO.getPromotionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "VoucherType not found with id: " + applyCategoriesDTO.getPromotionId()));

            List<ForProductType> forProductTypes = iForProductTypeRepository
                    .findAllByVoucherTypeAndCategoryIds(voucherType, applyCategoriesDTO.getProductIds());

            if (forProductTypes.isEmpty()) {
                throw new BadRequestException(" No valid ForProductType entries found for the given IDs");
            }

            iForProductTypeRepository.deleteAll(forProductTypes);

            return new ResponseData(HttpStatus.OK, "Categories removed from VoucherType successfully", null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to remove categories from VoucherType", e.getMessage());
        }
    }

    @Override
    public ResponseData getCategoriesNotInVoucherType(int voucherTypeId) {
        try {
            VoucherType voucherType = iVoucherTypeRepository.findById(voucherTypeId)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("VoucherType not found with id: " + voucherTypeId));

            List<Integer> categoryIdsInVoucher = iForProductTypeRepository.findByVoucherType(voucherType)
                    .stream()
                    .map(forProductType -> forProductType.getProductCategory().getId())
                    .collect(Collectors.toList());

            List<ProductCategoryDTO> categoryDTOs;
            if (categoryIdsInVoucher.isEmpty()) {
                categoryDTOs = iProductCategoryRepository.findAll()
                        .stream()
                        .map(ProductCategory::getDTO)
                        .collect(Collectors.toList());
            } else {
                categoryDTOs = iProductCategoryRepository.findByIdNotIn(categoryIdsInVoucher)
                        .stream()
                        .map(ProductCategory::getDTO)
                        .collect(Collectors.toList());
            }

            return new ResponseData(HttpStatus.OK, "Categories not in VoucherType found successfully", categoryDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get categories not in VoucherType", e.getMessage());
        }
    }

}
