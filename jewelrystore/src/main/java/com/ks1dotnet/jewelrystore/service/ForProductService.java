package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ForProductDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForProductRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPromotionGenericService;

import jakarta.transaction.Transactional;

@Service
public class ForProductService implements IPromotionGenericService<Product> {
    @Autowired
    IForProductRepository iForProductRepository;

    @Autowired
    IProductRepository iProductRepository;

    @Autowired
    IPromotionRepository iPromotionRepository;

    @Transactional
    @Override
    public ResponseData applyPromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productIds = applyPromotionDTO.getEntityIds();

            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new ApplicationException("Not found", HttpStatus.NOT_FOUND));
            PromotionDTO promotionDTO = promotion.getDTO();
            if (promotionDTO == null) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            } else if (!promotionDTO.getPromotionType().equals("product")) {
                throw new ApplicationException("Not allowed to apply this promotion type",
                        HttpStatus.NOT_ACCEPTABLE);
            }

            List<Product> products = iProductRepository.findAllById(productIds);
            if (products.isEmpty()) {
                throw new ApplicationException("No products found with the given ids",
                        HttpStatus.NOT_FOUND);
            }

            List<ForProduct> forProductsToSave = new ArrayList<>();
            for (Product product : products) {
                List<ForProduct> activePromotions = iForProductRepository
                        .findActiveProductPromotionsByProductIdAndInvoiceTypeId(product.getId(),
                                promotion.getInvoiceType().getId());

                for (ForProduct activePromotion : activePromotions) {
                    activePromotion.setStatus(false);
                    forProductsToSave.add(activePromotion);
                }

                ForProduct existingForProduct = iForProductRepository
                        .findByPromotionIdAndProductId(promotionId, product.getId());
                if (existingForProduct != null) {
                    existingForProduct.setStatus(true);
                    forProductsToSave.add(existingForProduct);
                } else {
                    forProductsToSave.add(new ForProduct(promotion, product, true));
                }
            }

            iForProductRepository.saveAll(forProductsToSave);
            List<ForProductDTO> forProductDTOs =
                    forProductsToSave.stream().map(ForProduct::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to products successfully",
                    forProductDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at applyPromotion ForProductService: " + e.getMessage(),
                    "Failed to apply promotion to products");
        }
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productIds = applyPromotionDTO.getEntityIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ApplicationException("Promotion not found with id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForProduct> forProducts =
                    iForProductRepository.findByPromotionIdAndProductIds(promotionId, productIds);

            if (forProducts.isEmpty()) {
                throw new ApplicationException(
                        "No products found with the given ids in the promotion",
                        HttpStatus.NOT_FOUND);
            }

            forProducts.forEach(forProduct -> forProduct.setStatus(false));
            iForProductRepository.saveAll(forProducts);

            return new ResponseData(HttpStatus.OK, "Promotion removed from products successfully",
                    null);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at removePromotion ForProductService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at removePromotion ForProductService: " + e.getMessage(),
                    "Failed to remove promotion from products");
        }
    }

    @Override
    public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
        try {
            List<ForProduct> forProducts = iForProductRepository
                    .findActiveProductPromotionsByProductIdAndInvoiceTypeId(entityId,
                            iPromotionRepository.findById(promotionId)
                                    .orElseThrow(() -> new ApplicationException(
                                            "Promotion not found", HttpStatus.NOT_FOUND))
                                    .getInvoiceType().getId());

            if (forProducts.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Product is not in any other active promotions", null);
            }

            List<PromotionDTO> otherPromotions =
                    forProducts.stream().filter(fg -> fg.getPromotion().getId() != promotionId)
                            .map(fg -> fg.getPromotion().getDTO()).collect(Collectors.toList());

            if (otherPromotions.isEmpty()) {
                return new ResponseData(HttpStatus.OK,
                        "Product is not in any other active promotions", null);
            } else {
                return new ResponseData(HttpStatus.CONFLICT,
                        "Product is active in other promotions", otherPromotions);
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForProductService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at checkInOtherActivePromotions ForProductService: " + e.getMessage(),
                    "Failed to check product in other active promotions");
        }
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(int promotionId) {
        try {
            List<Product> products = iForProductRepository.findProductsNotInPromotion(promotionId);
            if (products == null || products.isEmpty()) {
                throw new ApplicationException(
                        "No products found not in the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ProductDTO> productDTOs =
                    products.stream().map(Product::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Products not in promotion found successfully",
                    productDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForProductService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesNotInPromotion ForProductService: " + e.getMessage(),
                    "Failed to get products not in promotion");
        }
    }

    @Override
    public ResponseData getEntitiesInPromotion(int promotionId) {
        try {
            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new NotFoundException());
            List<ForProduct> forProducts =
                    iForProductRepository.findByPromotionId(promotion.getId());
            if (forProducts == null || forProducts.isEmpty()) {
                throw new ApplicationException(
                        "No products found for the given promotion id: " + promotionId,
                        HttpStatus.NOT_FOUND);
            }

            List<ForProductDTO> forProductDTOs =
                    forProducts.stream().map(ForProduct::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Products found successfully", forProductDTOs);
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForProductService: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getEntitiesInPromotion ForProductService: " + e.getMessage(),
                    "Failed to get products by promotion id");
        }
    }
}
