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
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialOfProductService;

@RestController
@RequestMapping("/material/ofProduct")
@CrossOrigin("*")
public class MaterialOfProductController {
    @Autowired
    private IMaterialOfProductService iMaterialOfProductService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData response = iMaterialOfProductService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        ResponseData response = iMaterialOfProductService.Page(0, 10);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("nextPage")
    public ResponseEntity<?> getAll(@RequestParam int page, @RequestParam int size) {
        ResponseData response = iMaterialOfProductService.Page(page, size);
        return new ResponseEntity<>(response, response.getStatus());
    }
}