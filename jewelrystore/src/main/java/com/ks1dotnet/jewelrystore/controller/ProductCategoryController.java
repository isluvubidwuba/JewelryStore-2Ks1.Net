package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductCategoryService;

@RestController
@RequestMapping("${apiURL}/product/category")
@CrossOrigin("*")
public class ProductCategoryController {
    @Autowired
    private IProductCategoryService iProductCategoryService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData response = iProductCategoryService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        ResponseData response = iProductCategoryService.Page(0, 20);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("nextPage")
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int size) {
        ResponseData response = iProductCategoryService.Page(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
