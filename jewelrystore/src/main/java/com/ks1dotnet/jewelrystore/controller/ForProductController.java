package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductService;

@RestController
@RequestMapping("/promotion-for-product")
@CrossOrigin("*")
public class ForProductController {

    @Autowired
    IForProductService iForProductService;

    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<ResponseData> getProductsByPromotion(@PathVariable int promotionId) {
        ResponseData responseData = iForProductService.getProductsByPromotionId(promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/not-in-promotion/{promotionId}")
    public ResponseEntity<ResponseData> getProductsNotInPromotion(@PathVariable int promotionId) {
        ResponseData responseData = iForProductService.getProductsNotInPromotion(promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/apply-promotion")
    public ResponseEntity<ResponseData> applyPromotionToProducts(@RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = iForProductService.applyPromotionToProducts(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/remove-promotion")
    public ResponseEntity<ResponseData> removePromotionFromProducts(@RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = iForProductService.removePromotionFromProducts(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/check-product/{productId}/{promotionId}")
    public ResponseEntity<ResponseData> checkProductInOtherActivePromotions(@PathVariable int productId,
            @PathVariable int promotionId) {
        ResponseData responseData = iForProductService.checkProductInOtherActivePromotions(productId, promotionId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

}
