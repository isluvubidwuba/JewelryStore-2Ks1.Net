package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.CustomerTypeService;

@RestController
@RequestMapping("${apiURL}/customertype")
@CrossOrigin("*")
public class CustomerTypeController {
    @Autowired
    private CustomerTypeService customerTypeService;

    @PostMapping("/updatepointcondition")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> updatePointCondition(@RequestParam Integer id,
            @RequestParam String type, @RequestParam Integer pointCondition) {
        ResponseData responseData =
                customerTypeService.updatePointCondition(id, type, pointCondition);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> addCustomerType(@RequestParam String type,
            @RequestParam Integer pointCondition) {
        ResponseData responseData = customerTypeService.addCustomerType(type, pointCondition);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteCustomerType(@RequestParam Integer customerTypeId) {
        ResponseData responseData =
                customerTypeService.deleteCustomerTypeAndUpdateRanks(customerTypeId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/findall")
    public ResponseEntity<?> findAll() {
        ResponseData responseData = customerTypeService.findAll();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
