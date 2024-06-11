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
// import com.ks1dotnet.jewelrystore.service.serviceImp.IForProductTypeService;

// @RestController
// @RequestMapping("/promotion-for-category")
// @CrossOrigin("*")
// public class ForProductTypeController {

// @Autowired
// IForProductTypeService iForProductTypeService;

// @GetMapping("/promotion/{promotionId}")
// public ResponseEntity<ResponseData> getCategoriesByPromotion(@PathVariable
// int promotionId) {
// ResponseData responseData =
// iForProductTypeService.getCategoriesByPromotionId(promotionId);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @PostMapping("/apply-promotion")
// public ResponseEntity<ResponseData> applyPromotionToCategories(@RequestBody
// ApplyPromotionDTO applyPromotionDTO) {
// ResponseData responseData =
// iForProductTypeService.applyPromotionToCategories(applyPromotionDTO);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @PostMapping("/remove-promotion")
// public ResponseEntity<ResponseData> removePromotionFromCategories(
// @RequestBody ApplyPromotionDTO applyPromotionDTO) {
// ResponseData responseData =
// iForProductTypeService.removePromotionFromCategories(applyPromotionDTO);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }

// @GetMapping("/check-category/{categoryId}/{promotionId}")
// public ResponseEntity<ResponseData>
// checkCategoryInOtherActivePromotions(@PathVariable int categoryId,
// @PathVariable int promotionId) {
// ResponseData responseData =
// iForProductTypeService.checkCategoryInOtherActivePromotions(categoryId,
// promotionId);
// return new ResponseEntity<>(responseData, HttpStatus.OK);
// }

// @GetMapping("/not-in-promotion/{promotionId}")
// public ResponseEntity<ResponseData> getCategoriesNotInPromotion(@PathVariable
// int promotionId) {
// ResponseData responseData =
// iForProductTypeService.getCategoriesNotInPromotion(promotionId);
// return new ResponseEntity<>(responseData, responseData.getStatus());
// }
// }
