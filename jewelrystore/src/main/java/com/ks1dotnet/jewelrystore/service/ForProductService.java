package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.ks1dotnet.jewelrystore.repository.IForProductTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IProductRepository;
import com.ks1dotnet.jewelrystore.repository.IPromotionRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductService;

@Service
public class ForProductService implements IForProductService {
    @Autowired
    IForProductRepository iForProductRepository;

    @Autowired
    IProductRepository iProductRepository;
    @Autowired
    IPromotionRepository iPromotionRepository;

    @Autowired
    IForProductTypeRepository iForProductTypeRepository;

    @Override
    public ResponseData getProductsByPromotionId(int promotionId) {
        try {
            List<ForProduct> forProducts = iForProductRepository.findProductsByPromotionId(promotionId);
            if (forProducts.isEmpty()) {
                throw new ResourceNotFoundException("No products found for the given promotion id: " + promotionId);
            }

            List<ForProductDTO> forProductDTOs = forProducts.stream().map(fp -> new ForProductDTO(
                    fp.getId(),
                    fp.getPromotion().getDTO(),
                    fp.getProduct().getDTO(),
                    fp.isStatus())).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Products found successfully", forProductDTOs);
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
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productIds = applyPromotionDTO.getProductIds();

            PromotionDTO promotionDTO = iPromotionRepository.findPromotionDTOById(applyPromotionDTO.getPromotionId());
            if (promotionDTO == null) {
                throw new ResourceNotFoundException("Promotion not found with id: " + promotionId);
            } else if (!promotionDTO.getPromotionType().equals("product")) {
                throw new BadRequestException("Not allow to applied");
            }

            List<Product> products = iProductRepository.findAllById(productIds);
            if (products.isEmpty()) {
                throw new BadRequestException("No products found with the given ids");
            }

            List<ForProduct> forProductsToSave = new ArrayList<>();
            for (Product product : products) {
                // Kiểm tra các khuyến mãi khác mà sản phẩm đang tham gia
                ResponseData checkResponse = checkProductInOtherActivePromotions(product.getId(), promotionId);
                if (checkResponse.getStatus() == HttpStatus.CONFLICT) {
                    List<PromotionDTO> otherPromotions = (List<PromotionDTO>) checkResponse.getData();
                    for (PromotionDTO otherPromotion : otherPromotions) {
                        // Tắt trạng thái của sản phẩm trong các khuyến mãi khác
                        ApplyPromotionDTO removeDTO = new ApplyPromotionDTO();
                        removeDTO.setPromotionId(otherPromotion.getId());
                        removeDTO.setProductIds(Collections.singletonList(product.getId()));
                        removePromotionFromProducts(removeDTO);
                    }
                }

                // Thêm hoặc cập nhật trạng thái khuyến mãi hiện tại cho sản phẩm
                ForProduct existingForProduct = iForProductRepository
                        .findByPromotionIdAndProductId(promotionId, product.getId());
                if (existingForProduct != null) {
                    // Update status to true if it exists
                    existingForProduct.setStatus(true);
                    forProductsToSave.add(existingForProduct);
                } else {
                    // Create new record if it does not exist
                    forProductsToSave.add(new ForProduct(new Promotion(promotionDTO), product, true));
                }
            }

            iForProductRepository.saveAll(forProductsToSave);
            List<ProductDTO> productDTOs = products.stream().map(Product::getDTO).collect(Collectors.toList());

            return new ResponseData(HttpStatus.OK, "Promotion applied to products successfully", productDTOs);
        } catch (Exception e) {
            throw new BadRequestException("Failed to apply promotion to products", e.getMessage());
        }
    }

    @Override
    public ResponseData removePromotionFromProducts(ApplyPromotionDTO applyPromotionDTO) {
        try {
            int promotionId = applyPromotionDTO.getPromotionId();
            List<Integer> productIds = applyPromotionDTO.getProductIds();

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
    public ResponseData checkProductInOtherActivePromotions(int productId, int currentPromotionId) {
        List<ForProduct> forProducts = iForProductRepository.findActiveProductPromotionsByProductId(productId);

        if (forProducts.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Product is not in any other active promotions", null);
        }

        List<PromotionDTO> otherPromotions = new ArrayList<>();
        for (ForProduct forProduct : forProducts) {
            if (forProduct.getPromotion().getId() != currentPromotionId) {
                otherPromotions.add(forProduct.getPromotion().getDTO());
            }
        }

        if (otherPromotions.isEmpty()) {
            return new ResponseData(HttpStatus.OK, "Product is not in any other active promotions", null);
        } else {
            return new ResponseData(HttpStatus.CONFLICT, "Product is active in other promotions", otherPromotions);
        }
    }

}
