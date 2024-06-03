package com.ks1dotnet.jewelrystore.controller;

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
        ResponseData ResponseData = new ResponseData();
        try {
            Employee employee = iAuthenticationService.findById(emp.getId());
            // SecretKey secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            // String encrypKey = Encoders.BASE64.encode(secretKey.getEncoded());
            // System.out.println(encrypKey);
            if (employee == null) {
                ResponseData.setDesc("SignUp fail. Not found employee");
                ResponseData.setStatus(HttpStatus.NOT_FOUND);

            } else if (passwordEncoder.matches(emp.getPinCode(), employee.getPinCode())) {
                String token = jwtUtilsHelper.generateToken(employee.getId(), employee.getRole().getName());
                ResponseData.setDesc("SignUp successful");
                ResponseData.setStatus(HttpStatus.OK);
                ResponseData.setData(token);
            } else {
                ResponseData.setDesc("SignUp fail. Pincode Error");
                ResponseData.setStatus(HttpStatus.NOT_FOUND);
                ResponseData.setData("");

            }
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("SignUp fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }
}