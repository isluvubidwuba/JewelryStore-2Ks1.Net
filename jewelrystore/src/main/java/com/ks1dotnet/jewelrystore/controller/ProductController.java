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

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam String id) {
        try {
            ProductDTO product = iProductService.findById(id);
            return new ResponseEntity<>(new responseData(201, "Get successfully", product), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get product error: " + e.getMessage(), null),
                    HttpStatus.OK);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<ProductDTO> product = iProductService.findAll();
            return new ResponseEntity<>(new responseData(201, "Get successfully", product), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get product failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
