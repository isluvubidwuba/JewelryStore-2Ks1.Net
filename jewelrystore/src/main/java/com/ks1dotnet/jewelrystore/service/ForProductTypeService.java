package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForProductTypeDTO;
import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForProductType;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForProductTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductTypeService;

import jakarta.transaction.Transactional;

@Service
public class ForProductTypeService implements IForProductTypeService {

    @Autowired
    IForProductTypeRepository iForProductTypeRepository;

    @Autowired
    IProductCategoryRepository iProductCategoryRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Override
    @Transactional // Thêm @Transactional
    public ResponseData getCategoriesByPromotionId(int promotionId) {
        try {
            List<ForProductType> forProductTypes = iForProductTypeRepository.findCategoriesByPromotionId(promotionId);
            if (forProductTypes == null || forProductTypes.isEmpty()) {
                throw new ResourceNotFoundException("No categories found for the given promotion id: " + promotionId);
            }

            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(promotionId);
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            }
            if (!promotionDTO.getPromotionType().equals("category")) {
                throw new BadRequestException("Not allow");
            }

            List<ForProductTypeDTO> forProductTypeDTOs = forProductTypes.stream().map(fp -> fp.getDTO())
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Categories found successfully", forProductTypeDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get categories by promotion id", e.getMessage());
        }
    }

    @Override
    @Transactional // Thêm @Transactional
    public ResponseData applyPromotionToCategories(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> categoryIds = applyPromotionDTO.getProductIds();

            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(promotionId);
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            } else if (!promotionDTO.getPromotionType().equals("category")) {
                throw new BadRequestException("Not allow to apply this promotion type");
            }

            List<ProductCategory> categories = iProductCategoryRepository.findAllById(categoryIds);
            if (categories.isEmpty()) {
                throw new BadRequestException("No categories found with the given ids");
            }

            List<ForProductType> forProductTypesToSave = new ArrayList<>();
            for (ProductCategory category : categories) {
                // Kiểm tra các khuyến mãi khác mà danh mục đang tham gia
                ResponseData checkResponse = checkCategoryInOtherActivePromotions(category.getId(), promotionId);
                if (checkResponse.getStatus() == HttpStatus.CONFLICT) {
                    List<PromotionDTO> otherPromotions = (List<PromotionDTO>) checkResponse.getData();
                    for (PromotionDTO otherPromotion : otherPromotions) {
                        // Tắt trạng thái của danh mục trong các khuyến mãi khác
                        ApplyPromotionDTO removeDTO = new ApplyPromotionDTO();
                        removeDTO.setPromotionId(otherPromotion.getId());
                        removeDTO.setProductIds(Collections.singletonList(category.getId()));
                        removePromotionFromCategories(removeDTO);
                    }
                }

                // Thêm hoặc cập nhật trạng thái khuyến mãi hiện tại cho danh mục
                ForProductType existingForProductType = iForProductTypeRepository
                        .findByPromotionIdAndCategoryId(promotionId, category.getId());
                if (existingForProductType != null) {
                    // Update status to true if it exists
                    existingForProductType.setStatus(true);
                    forProductTypesToSave.add(existingForProductType);
                } else {
                    // Create new record if it does not exist
                    forProductTypesToSave.add(new ForProductType(new Promotion(promotionDTO), category, true));
                }
            }

            iForProductTypeRepository.saveAll(forProductTypesToSave);
            List<ForProductTypeDTO> forProductTypeDTOs = forProductTypesToSave.stream()
                    .map(fp -> fp.getDTO())
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to categories successfully", forProductTypeDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply promotion to categories", e.getMessage());
        }
    }

    @Override
    public ResponseData removePromotionFromCategories(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> categoryIds = applyPromotionDTO.getProductIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            }

            List<ForProductType> forProductTypes = iForProductTypeRepository
                    .findByPromotionIdAndCategoryIds(promotionId, categoryIds);

            if (forProductTypes.isEmpty()) {
                throw new BadRequestException("No categories found with the given ids in the promotion");
            }

            forProductTypes.forEach(forProductType -> forProductType.setStatus(false));
            iForProductTypeRepository.saveAll(forProductTypes);

            return new ResponseData(HttpStatus.OK, "Promotion removed from categories successfully", null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to remove promotion from categories", e.getMessage());
        }
    }

    @Override
    public ResponseData checkCategoryInOtherActivePromotions(int categoryId, int currentPromotionId) {
        List<ForProductType> forProductTypes = iForProductTypeRepository
                .findActiveCategoryPromotionsByCategoryId(categoryId);

        if (forProductTypes.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Category is not in any other active promotions", null);
        }

        List<PromotionDTO> otherPromotions = new ArrayList<>();
        for (ForProductType forProductType : forProductTypes) {
            if (forProductType.getPromotion().getId() != currentPromotionId) {
                otherPromotions.add(forProductType.getPromotion().getDTO());
            }
        }

        if (otherPromotions.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Category is not in any other active promotions", null);
        } else {
            return new ResponseData(HttpStatus.CONFLICT, "Category is active in other promotions", otherPromotions);
        }
    }

    @Override
    public ResponseData getCategoriesNotInPromotion(int promotionId) {
        try {
            List<ProductCategory> categories = iForProductTypeRepository.findCategoriesNotInPromotion(promotionId);
            List<ProductCategoryDTO> categoryDTOs = categories.stream().map(ProductCategory::getDTO)
                    .collect(Collectors.toList());
            return new ResponseData(HttpStatus.OK, "Categories not in promotion found successfully", categoryDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get categories not in promotion", e.getMessage());
        }
    }
}
