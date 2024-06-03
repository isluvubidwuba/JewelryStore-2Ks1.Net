package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.CustomerTypeService;

@RestController
@RequestMapping("/customertype")
@CrossOrigin("*")
public class CustomerTypeController {
    @Autowired
    private CustomerTypeService customerTypeService;

    @PostMapping("/updatepointcondition")
    public ResponseData updatePointCondition(
            @RequestParam Integer id,
            @RequestParam String type,
            @RequestParam Integer pointCondition) {
        return customerTypeService.updatePointCondition(id, type, pointCondition);
    }

    @PostMapping("/add")
    public ResponseData addCustomerType(
            @RequestParam String type,
            @RequestParam Integer pointCondition) {
        return customerTypeService.addCustomerType(type, pointCondition);
    }

    @PostMapping("/delete")
    public ResponseData deleteCustomerType(@RequestParam Integer customerTypeId) {
        return customerTypeService.deleteCustomerTypeAndUpdateRanks(customerTypeId);
    }

    @GetMapping("/findall")
    public ResponseData findAll() {
        return customerTypeService.findAll();
    }

}
