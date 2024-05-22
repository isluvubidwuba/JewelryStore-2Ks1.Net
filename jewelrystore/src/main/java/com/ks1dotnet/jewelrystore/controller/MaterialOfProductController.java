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

import com.ks1dotnet.jewelrystore.dto.MaterialOfProductDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialOfProductService;

@RestController
@RequestMapping("/material/ofProduct")
@CrossOrigin("*")
public class MaterialOfProductController {
    @Autowired
    private IMaterialOfProductService iMaterialOfProductService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            MaterialOfProductDTO m = iMaterialOfProductService.findById(id);
            return new ResponseEntity<>(new responseData(201, "Get material of product successfully", m),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, "Get material of product failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<MaterialOfProductDTO> listM = iMaterialOfProductService.findAll();
            return new ResponseEntity<>(new responseData(201, "Get list material of product successfully", listM),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new responseData(400, "Get list material of product failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
