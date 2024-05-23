package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IProductService;

@RestController
@RequestMapping("/product")
@CrossOrigin("*")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam String id) {
        ResponseData response = iProductService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        ResponseData response = iProductService.findAll();
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("change")
    public ResponseEntity<?> update(@RequestBody ProductDTO t) {
        ResponseData response = iProductService.update(t);
        return new ResponseEntity<>(response, response.getStatus());
    }

    // view : update status
    // status = 1 disable, status = 0 is disable
    @PostMapping("change/status")
    public ResponseEntity<?> update(@RequestParam String id, @RequestParam int status) {
        ResponseData response = iProductService.updateStatus(id, status);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("change/statusAll")
    public ResponseEntity<?> update(@RequestBody Map<String, Integer> map) {
        ResponseData response = iProductService.updateStatus(map);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ProductDTO t) {
        ResponseData response = iProductService.insert(t);
        return new ResponseEntity<>(response, response.getStatus());
    }

}
