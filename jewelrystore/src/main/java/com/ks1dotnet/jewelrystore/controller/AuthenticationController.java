package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

@RestController
@RequestMapping("/authentication")
@CrossOrigin("*")
public class AuthenticationController {
    @Autowired
    private IAuthenticationService iAuthenticationService;

    @Autowired
    private JwtUtilsHelper jwtUtilsHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    private ResponseEntity<?> login(@RequestBody Employee emp) {
        ResponseData responseData = new ResponseData();
        try {
            Employee employee = iAuthenticationService.findById(emp.getId());
            if (employee == null) {
                responseData.setDesc("SignUp fail. Not found employee");
                responseData.setStatus(HttpStatus.NOT_FOUND);
            } else if (passwordEncoder.matches(emp.getPinCode(), employee.getPinCode())) {
                String token = jwtUtilsHelper.generateToken(employee.getId(), employee.getRole().getName());
                responseData.setDesc("SignUp successful");
                responseData.setStatus(HttpStatus.OK);

                // Trả về token, id và role của người dùng
                Map<String, Object> responseDataMap = new HashMap<>();
                responseDataMap.put("token", token);
                responseDataMap.put("id", employee.getId());
                responseDataMap.put("role", employee.getRole().getName());
                responseData.setData(responseDataMap);

            } else {
                responseData.setDesc("SignUp fail. Pincode Error");
                responseData.setStatus(HttpStatus.NOT_FOUND);
                responseData.setData("");
            }
        } catch (Exception e) {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("SignUp fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

}