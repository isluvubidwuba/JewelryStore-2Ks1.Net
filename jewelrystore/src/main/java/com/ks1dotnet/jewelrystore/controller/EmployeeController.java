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
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeController {
    @Autowired
    private IEmployeeService iEmployeeService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        responseData responseData = new responseData();
        List<Employee> listEmpl = iEmployeeService.findAll();
        responseData.setData(listEmpl);
        // for (employee e : listEmpl) {
        // System.out.println(e);
        System.out.println(iEmployeeService.findByPinCode("12"));
        // }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/signup")
    private ResponseEntity<?> login(@RequestBody Employee emp) {
        responseData responseData = new responseData();
        try {
            Employee employee = iEmployeeService.findById(emp.getId());

            if (employee == null) {
                responseData.setDesc("SignUp fail. Not found employee");
            } else if (employee.getPinCode().equals(emp.getPinCode())) {
                responseData.setDesc("SignUp successful");
            } else {
                responseData.setDesc("SignUp fail. Pincode Error");
            }
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("SignUp fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
