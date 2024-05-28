package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IVoucherTypeService;

@RestController
@RequestMapping("voucher")
@CrossOrigin("*")
public class VoucherTypeController {
    @Autowired
    private IVoucherTypeService iVoucherTypeService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        ResponseData ResponseData = new ResponseData();
        ResponseData.setData(iVoucherTypeService.findAll());
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }
}