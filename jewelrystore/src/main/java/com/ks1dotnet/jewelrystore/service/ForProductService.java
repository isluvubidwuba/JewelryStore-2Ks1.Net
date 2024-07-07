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
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
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
                    .orElseThrow(() -> new BadRequestException("Not found"));
            PromotionDTO promotionDTO = promotion.getDTO();
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            } else if (!promotionDTO.getPromotionType().equals("product")) {
                throw new BadRequestException("Not allowed to apply this promotion type");
            }

            List<Product> products = iProductRepository.findAllById(productIds);
            if (products.isEmpty()) {
                throw new BadRequestException("No products found with the given ids");
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
            List<ForProductDTO> forProductDTOs = forProductsToSave.stream()
                    .map(ForProduct::getDTO)
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to products successfully",
                    forProductDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply promotion to products", e.getMessage());
        }
    }

    @Override
    public ResponseData removePromotion(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productIds = applyPromotionDTO.getEntityIds();

            if (!iPromotionRepository.existsById(promotionId)) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            }

            List<ForProduct> forProducts = iForProductRepository
                    .findByPromotionIdAndProductIds(promotionId, productIds);

            if (forProducts.isEmpty()) {
                throw new BadRequestException("No products found with the given ids in the promotion");
            }

            forProducts.forEach(forProduct -> forProduct.setStatus(false));
            iForProductRepository.saveAll(forProducts);

            return new ResponseData(HttpStatus.OK, "Promotion removed from products successfully", null);
        } catch (Exception e) {
            throw new BadRequestException("Failed to remove promotion from products", e.getMessage());
        }
    }

    @Override
    public ResponseData checkInOtherActivePromotions(int entityId, int promotionId) {
        try {
            List<ForProduct> forProducts = iForProductRepository
                    .findActiveProductPromotionsByProductIdAndInvoiceTypeId(entityId,
                            iPromotionRepository.findById(promotionId)
                                    .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"))
                                    .getInvoiceType().getId());

            if (forProducts.isEmpty()) {
                return new ResponseData(HttpStatus.OK, "Product is not in any other active promotions", null);
            }

            List<PromotionDTO> otherPromotions = forProducts.stream()
                    .filter(fg -> fg.getPromotion().getId() != promotionId)
                    .map(fg -> fg.getPromotion().getDTO())
                    .collect(Collectors.toList());

            if (otherPromotions.isEmpty()) {
                return new ResponseData(HttpStatus.OK, "Product is not in any other active promotions", null);
            } else {
                return new ResponseData(HttpStatus.CONFLICT, "Product is active in other promotions",
                        otherPromotions);
            }
        } catch (Exception e) {
            throw new BadRequestException("Failed to check product in other active promotions",
                    e.getMessage());
        }
    }

    @Override
    public ResponseData getEntitiesNotInPromotion(int promotionId) {
        try {
            List<Product> products = iForProductRepository
                    .findProductsNotInPromotion(promotionId);
            if (products == null || products.isEmpty()) {
                throw new ResourceNotFoundException(
                        "No products found not in the given promotion id: " + promotionId);
            }

            List<ProductDTO> productDTOs = products.stream()
                    .map(Product::getDTO)
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Products not in promotion found successfully",
                    productDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get products not in promotion", e.getMessage());
        }
    }

    @Override
    public ResponseData getEntitiesInPromotion(int promotionId) {
        try {
            Promotion promotion = iPromotionRepository.findById(promotionId)
                    .orElseThrow(() -> new NotFoundException());
            List<ForProduct> forProducts = iForProductRepository.findByPromotionId(promotion.getId());
            if (forProducts == null || forProducts.isEmpty()) {
                return new ResponseData(HttpStatus.BAD_REQUEST,
                        "No products found for the given promotion id: " + promotionId, null);

            }
            List<ForProductDTO> forProductDTOs = forProducts.stream()
                    .map(ForProduct::getDTO)
                    .collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Products found successfully", forProductDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get products by promotion id", e.getMessage());
        }
    }
}
