package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForProductTypeDTO;
import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForProductType;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForProductTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IProductCategoryRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;
import jakarta.transaction.Transactional;

@Service
public class ForProductTypeService implements IPromotionGenericService<ProductCategory> {

    @Autowired
    IForProductTypeRepository iForProductTypeRepository;

    @Autowired
    IProductCategoryRepository iProductCategoryRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Transactional
    @Override
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productCategoryIds = applyPromotionDTO.getEntityIds();

            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new ApplicationException("Not found"));
            PromotionDTO promotionDTO = promotion.getDTO();
            if (promotionDTO == null) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            } else if (!promotionDTO.getPromotionType().equals("category")) {
                throw new ApplicationException("Not allowed to apply this promotion type",
                        HttpStatus.NOT_ACCEPTABLE);
            }

            List<ProductCategory> productCategories =
                    iProductCategoryRepository.findAllById(productCategoryIds);
            if (productCategories.isEmpty()) {
                throw new ApplicationException("No product categories found with the given ids",
                        HttpStatus.NOT_FOUND);
            }

            List<ForProductType> forProductTypesToSave = new ArrayList<>();
            for (ProductCategory productCategory : productCategories) {
                List<ForProductType> activePromotions = iForProductTypeRepository
                        .findActiveProductTypePromotionsByProductCategoryIdAndInvoiceTypeId(
                                productCategory.getId(), promotion.getInvoiceType().getId());

                for (ForProductType activePromotion : activePromotions) {
                    activePromotion.setStatus(false);
                    forProductTypesToSave.add(activePromotion);
                }

                ForProductType existingForProductType =
                        iForProductTypeRepository.findByPromotionIdAndProductCategoryId(promotionId,
                                productCategory.getId());
                if (existingForProductType != null) {
                    existingForProductType.setStatus(true);
                    forProductTypesToSave.add(existingForProductType);
                } else {
                    forProductTypesToSave.add(new ForProductType(promotion, productCategory, true));
                }
            }

            iForProductTypeRepository.saveAll(forProductTypesToSave);
            List<ForProductTypeDTO> forProductTypeDTOs = forProductTypesToSave.stream()
                    .map(ForProductType::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK,
                    "Promotion applied to product categories successfully", forProductTypeDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductTypeService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductTypeService: " + e.getMessage(),
                    "Failed to apply promotion to product categories");
        }
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productCategoryIds = applyPromotionDTO.getEntityIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForProductType> forProductTypes = iForProductTypeRepository
                    .findByPromotionIdAndProductCategoryIds(promotionId, productCategoryIds);

            if (forProductTypes.isEmpty()) {
                throw new ApplicationException(
                        "No product categories found with the given ids in the promotion",
                        HttpStatus.NOT_FOUND);
            }

            forProductTypes.forEach(forProductType -> forProductType.setStatus(false));
            iForProductTypeRepository.saveAll(forProductTypes);

            return new ResponseData(HttpStatus.OK,
                    "Promotion removed from product categories successfully", null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductTypeService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductTypeService: " + e.getMessage(),
                    "Failed to remove promotion from product categories");
        }
    }

    @Override
    public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
        try {
            List<ForProductType> forProductTypes = iForProductTypeRepository
                    .findActiveProductTypePromotionsByProductCategoryIdAndInvoiceTypeId(entityId,
                            iPromotionRepository.findById(promotionId)
                                    .orElseThrow(() -> new ApplicationException(
                                            "Promotion not found", HttpStatus.NOT_FOUND))
                                    .getInvoiceType().getId());

            if (forProductTypes.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Product category is not in any other active promotions", null);
            }

            List<PromotionDTO> otherPromotions =
                    forProductTypes.stream().filter(fg -> fg.getPromotion().getId() != promotionId)
                            .map(fg -> fg.getPromotion().getDTO()).collect(Collectors.toList());

            if (otherPromotions.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Product category is not in any other active promotions", null);
            } else {
                return new ResponseData(HttpStatus.CONFLICT,
                        "Product category is active in other promotions", otherPromotions);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForProductTypeService: "
                            + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForProductTypeService: "
                            + e.getMessage(),
                    "Failed to check product category in other active promotions");
        }
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(int promotionId) {
        try {
            List<ProductCategory> productCategories =
                    iForProductTypeRepository.findProductCategoriesNotInPromotion(promotionId);
            if (productCategories == null || productCategories.isEmpty()) {
                throw new ApplicationException(
                        "No product categories found not in the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ProductCategoryDTO> productCategoryDTOs = productCategories.stream()
                    .map(ProductCategory::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK,
                    "Product categories not in promotion found successfully", productCategoryDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForProductTypeService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForProductTypeService: " + e.getMessage(),
                    "Failed to get product categories not in promotion");
        }
    }

    @Override
    public ResponseData getEntitiesInPromotion(int promotionId) {
        try {
            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new NotFoundException());
            List<ForProductType> forProductTypes =
                    iForProductTypeRepository.findByPromotionId(promotion.getId());
            if (forProductTypes == null || forProductTypes.isEmpty()) {
                throw new ApplicationException(
                        "No product categories found for the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForProductTypeDTO> forProductTypeDTOs = forProductTypes.stream()
                    .map(ForProductType::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Product categories found successfully",
                    forProductTypeDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForProductTypeService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForProductTypeService: " + e.getMessage(),
                    "Failed to get product categories by promotion id");
        }
    }
}
