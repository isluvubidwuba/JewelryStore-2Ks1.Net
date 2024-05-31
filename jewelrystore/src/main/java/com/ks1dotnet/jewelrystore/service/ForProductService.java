package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.Product;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IForProductRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductService;

@Service
public class ForProductService implements IForProductService {
    @Autowired
    IForProductRepository iForProductRepository;
    @Autowired
    IPromotionRepository iPromotionRepository;
    @Autowired
    IProductRepository iProductRepository;

    @Override
    public ResponseData getProductsByPromotionId(int promotionId) {
        try {
            List<Product> products = iForProductRepository.findProductsByPromotionId(promotionId);
            if (products.isEmpty()) {
                throw new ResourceNotFoundException("No products found for the given promotion id: " + promotionId);
            }
            List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());
            return new ResponseData(HttpStatus.OK, "Products found successfully", productDTOs);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Failed to get products by promotion id", e.getMessage());
        }
    }

    @Override
    public ResponseData getProductsNotInPromotion(int promotionId) {
        try {
            List<Product> products = iForProductRepository.findProductsNotInPromotion(promotionId);
            if (products.isEmpty()) {
                throw new BadRequestException(
                        "No products found that are not in the given promotion id: " + promotionId);
            }
            List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());
            return new ResponseData(HttpStatus.OK, "Products not in the given promotion found successfully",
                    productDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to get products not in the given promotion", e.getMessage());
        }
    }

    @Override
    public ResponseData applyPromotionToProducts(ApplyPromotionDTO applyPromotionDTO) {
        try {
            if (!iPromotionRepository.existsById(applyPromotionDTO.getPromotionId())) {
                throw new ResourceNotFoundException(
                        "Promotion not found with id: " + applyPromotionDTO.getPromotionId());
            }

            List<Product> products = iProductRepository.findAllById(applyPromotionDTO.getProductIds());

            if (products.isEmpty()) {
                throw new BadRequestException("No products found with the given ids");
            }

            List<ForProduct> forProducts = products.stream()
                    .map(product -> new ForProduct(new Promotion(applyPromotionDTO.getPromotionId()), product))
                    .collect(Collectors.toList());
            iForProductRepository.saveAll(forProducts);

            List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());
            return new ResponseData(HttpStatus.OK, "Promotion applied to products successfully", productDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply promotion to products", e.getMessage());
        }
    }

    @Override
    public ResponseData removePromotionFromProducts(ApplyPromotionDTO applyPromotionDTO) {
        try {
            if (!iPromotionRepository.existsById(applyPromotionDTO.getPromotionId())) {
                throw new ResourceNotFoundException(
                        "Promotion not found with id: " + applyPromotionDTO.getPromotionId());
            }

            iForProductRepository.deleteByPromotionIdAndProductIds(applyPromotionDTO.getPromotionId(),
                    applyPromotionDTO.getProductIds());

            return new ResponseData(HttpStatus.OK, "Promotion removed from products successfully", HttpStatus.OK);
        } catch (Exception e) {
            throw new BadRequestException("Failed to remove promotion from products", e.getMessage());
        }
    }

}
