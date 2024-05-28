package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;

@RestController
@RequestMapping("/authentication")
@CrossOrigin("*")
public class AuthenticationController {
    @Autowired
    private IAuthenticationService iAuthenticationService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        ResponseData ResponseData = new ResponseData();
        List<Employee> listEmpl = iAuthenticationService.findAll();
        Employee e = new Employee();
        ResponseData.setData(listEmpl);
        // for (employee e : listEmpl) {
        // System.out.println(e);
        System.out.println(iAuthenticationService.findByPinCode("12"));
        // }
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/signup")
    private ResponseEntity<?> login(@RequestBody Employee emp) {
        ResponseData ResponseData = new ResponseData();
        try {
            Employee employee = iAuthenticationService.findById(emp.getId());

            if (employee == null) {
                ResponseData.setDesc("SignUp fail. Not found employee");
            } else if (employee.getPinCode().equals(emp.getPinCode())) {
                ResponseData.setDesc("SignUp successful");
            } else {
                ResponseData.setDesc("SignUp fail. Pincode Error");
            }
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("SignUp fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }
}