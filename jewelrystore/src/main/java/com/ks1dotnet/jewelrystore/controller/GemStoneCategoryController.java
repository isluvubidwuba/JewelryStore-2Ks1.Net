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

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IGemStoneCategoryService;

@RestController
@RequestMapping("/gemStone/category")
@CrossOrigin("*")
public class GemStoneCategoryController {
    @Autowired
    private IGemStoneCategoryService iGemStoneCategoryService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            GemStoneCategoryDTO gc = iGemStoneCategoryService.findById(id);
            return new ResponseEntity<>(new responseData(201, "Get gem stone category successfully", gc),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(401, "Get gem stone category error: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("all")
    public ResponseEntity<?> getAll() {
        try {
            List<GemStoneCategoryDTO> listGc = iGemStoneCategoryService.findAll();
            return new ResponseEntity<>(new responseData(201, "Get list gem stone category successfully", listGc),
                    HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get list gem stone category error", null),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
