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

import com.ks1dotnet.jewelrystore.dto.MaterialDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IMaterialService;

@RestController
@RequestMapping("/material")
@CrossOrigin("*")
public class MaterialController {
    @Autowired
    private IMaterialService iMaterialService;

    @GetMapping
    public ResponseEntity<?> getById(@RequestParam int id) {
        try {
            MaterialDTO m = iMaterialService.findById(id);
            return new ResponseEntity<>(new responseData(201, "Get material successfully", m), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get material failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll() {
        try {
            List<MaterialDTO> listM = iMaterialService.findAll();
            return new ResponseEntity<>(new responseData(201, "Get list material successfully", listM), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new responseData(400, "Get list material failed: " + e.getMessage(), null),
                    HttpStatus.BAD_REQUEST);
        }
    }
}
