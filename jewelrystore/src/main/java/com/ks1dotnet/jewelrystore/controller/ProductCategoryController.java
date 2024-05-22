package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductCategoryService;

@RestController
@RequestMapping("/product/category")
@CrossOrigin("*")
public class ProductCategoryController {
    @Autowired
    private IProductCategoryService iProductCategoryService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            ProductCategoryDTO pc = iProductCategoryService.findById(id);
            return new ResponseEntity<>(new responseData(201, "Get product category successfully", pc), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get product category failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<ProductCategoryDTO> listPc = iProductCategoryService.findAll();
            return new ResponseEntity<>(new responseData(201, "Get list product category successfully", listPc),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, "Get list product category failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
