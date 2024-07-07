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
import com.ks1dotnet.jewelrystore.Enum.EntityType;
import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGenericPromotionService;

@RestController
@RequestMapping("${apiURL}/promotion-generic")
@CrossOrigin("*")
public class GenericPromotionController {
    @Autowired
    private IGenericPromotionService genericPromotionService;

    @PostMapping("/apply")
    public ResponseEntity<ResponseData> applyPromotion(
            @RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = genericPromotionService.applyPromotion(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/remove")
    public ResponseEntity<ResponseData> removePromotion(
            @RequestBody ApplyPromotionDTO applyPromotionDTO) {
        ResponseData responseData = genericPromotionService.removePromotion(applyPromotionDTO);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/check/{entityType}/{entityId}/{promotionId}")
    public ResponseEntity<ResponseData> checkEntityInOtherPromotions(
            @PathVariable EntityType entityType, @PathVariable int entityId,
            @PathVariable int promotionId) {
        ResponseData responseData = genericPromotionService.checkEntityInOtherPromotions(entityType,
                entityId, promotionId);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/not-in-promotion/{entityType}/{promotionId}")
    public ResponseEntity<ResponseData> getEntitiesNotInPromotion(
            @PathVariable EntityType entityType, @PathVariable int promotionId) {
        ResponseData responseData =
                genericPromotionService.getEntitiesNotInPromotion(entityType, promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/in-promotion/{entityType}/{promotionId}")
    public ResponseEntity<ResponseData> getEntitiesInPromotion(@PathVariable EntityType entityType,
            @PathVariable int promotionId) {
        ResponseData responseData =
                genericPromotionService.getEntitiesInPromotion(entityType, promotionId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }
}
