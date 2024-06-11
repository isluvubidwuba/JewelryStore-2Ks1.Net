// package com.ks1dotnet.jewelrystore.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.ks1dotnet.jewelrystore.dto.ApplyPromotionDTO;
// import com.ks1dotnet.jewelrystore.payload.ResponseData;
// import com.ks1dotnet.jewelrystore.service.serviceImp.IForGemStoneTypeService;

// @RestController
// @RequestMapping("/promotion-for-gemstone")
// @CrossOrigin("*")
// public class ForGemStoneTypeController {

// @Autowired
// IForGemStoneTypeService iForGemStoneTypeService;

// @GetMapping("/promotion/{promotionId}")
// public ResponseEntity<ResponseData> getGemStoneTypesByPromotion(@PathVariable
// int promotionId) {
// ResponseData responseData =
// iForGemStoneTypeService.getGemStoneTypesByPromotionId(promotionId);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @PostMapping("/apply-promotion")
// public ResponseEntity<ResponseData> applyPromotionToGemStoneTypes(
// @RequestBody ApplyPromotionDTO applyPromotionDTO) {
// ResponseData responseData =
// iForGemStoneTypeService.applyPromotionToGemStoneTypes(applyPromotionDTO);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @PostMapping("/remove-promotion")
// public ResponseEntity<ResponseData> removePromotionFromGemStoneTypes(
// @RequestBody ApplyPromotionDTO applyPromotionDTO) {
// ResponseData responseData =
// iForGemStoneTypeService.removePromotionFromGemStoneTypes(applyPromotionDTO);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @GetMapping("/check-gemstone/{gemstoneTypeId}/{promotionId}")
// public ResponseEntity<ResponseData>
// checkGemStoneTypeInOtherActivePromotions(@PathVariable int gemstoneTypeId,
// @PathVariable int promotionId) {
// ResponseData responseData =
// iForGemStoneTypeService.checkGemStoneTypeInOtherActivePromotions(gemstoneTypeId,
// promotionId);
// return new ResponseEntity<>(responseData, HttpStatus.OK);
// }

// @GetMapping("/not-in-promotion/{promotionId}")
// public ResponseEntity<ResponseData>
// getGemStoneTypesNotInPromotion(@PathVariable int promotionId) {
// ResponseData responseData =
// iForGemStoneTypeService.getGemStoneTypesNotInPromotion(promotionId);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }
// }
