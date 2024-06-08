package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;

@RestController
@RequestMapping("/gemStone/category")
@CrossOrigin("*")
public class GemStoneCategoryController {
    @Autowired
    private IGemStoneCategoryService iGemStoneCategoryService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        ResponseData response = iGemStoneCategoryService.findById(id);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        ResponseData response = iGemStoneCategoryService.Page(0, 10);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/update")
    public ResponseEntity<?> update(@RequestBody GemStoneCategoryDTO t) {
        ResponseData response = iGemStoneCategoryService.update(t);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody GemStoneCategoryDTO t) {
        ResponseData response = iGemStoneCategoryService.insert(t);
        return new ResponseEntity<>(response, response.getStatus());
    }
}
