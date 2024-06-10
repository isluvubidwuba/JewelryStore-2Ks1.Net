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
import com.ks1dotnet.jewelrystore.service.serviceImp.IForCustomerService;

@RestController
@RequestMapping("/promotion-for-customer")
@CrossOrigin("*")
public class ForCustomerController {

    @Autowired
    IForCustomerService iForCustomerService;

    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<ResponseData> getCustomerTypesByPromotion(@PathVariable int promotionId) {
        ResponseData responseData = iForCustomerService.getCustomerTypesByPromotionId(promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/apply-promotion")
    public ResponseEntity<ResponseData> applyPromotionToCustomerTypes(
            @RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = iForCustomerService.applyPromotionToCustomerTypes(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/remove-promotion")
    public ResponseEntity<ResponseData> removePromotionFromCustomerTypes(
            @RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = iForCustomerService.removePromotionFromCustomerTypes(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/check-customer/{customerTypeId}/{promotionId}")
    public ResponseEntity<ResponseData> checkCustomerTypeInOtherActivePromotions(@PathVariable int customerTypeId,
            @PathVariable int promotionId) {
        ResponseData responseData = iForCustomerService.checkCustomerTypeInOtherActivePromotions(customerTypeId,
                promotionId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/not-in-promotion/{promotionId}")
    public ResponseEntity<ResponseData> getCustomerTypesNotInPromotion(@PathVariable int promotionId) {
        ResponseData responseData = iForCustomerService.getCustomerTypesNotInPromotion(promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }
}
