package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

@RestController
@RequestMapping("/userinfo")
@CrossOrigin("*")
public class UserInfoController {
    @Autowired
    private IUserInfoService iUserInfoService;

    @GetMapping("/listpage")
    private ResponseEntity<?> getHomePageUser(
            @RequestParam int page) {
        System.out.println("Requested page: " + page);
        responseData responseData = new responseData();
        responseData.setData(iUserInfoService.getHomePageUser(page));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertUserInfo(
            @RequestParam MultipartFile file,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam int roleId,
            @RequestParam String address) {
                
        System.out.println("Inserting employee: " + fullName);
        responseData responseData = new responseData();
        boolean isSuccess = iUserInfoService.insertEmployee(file, fullName, phoneNumber, email, roleId, address);

        if (isSuccess) {
            responseData.setDesc("Insert successfull");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setData(500);
            responseData.setDesc("Insert fail. Internal Server Error");
            return new ResponseEntity<>("Employee created successfully", HttpStatus.CREATED);
        }

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam MultipartFile file,
            @RequestParam int id,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam int roleId,
            @RequestParam String address) {
        responseData responseData = new responseData();

        UserInfoDTO userInfoDTO = iUserInfoService.updateUserInfo(file, id, fullName, phoneNumber, email, roleId,
                address);
        if (userInfoDTO != null) {
            responseData.setDesc("Update successful");
            responseData.setData(userInfoDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(500);
            responseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listcustomer")
    public ResponseEntity<?> listCustomer(@RequestParam int page) {
        responseData responseData = new responseData();
        try {
            Map<String, Object> customers = iUserInfoService.listCustomer(page);
            responseData.setData(customers);
            responseData.setDesc("Fetch successful");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listsupplier")
    public ResponseEntity<?> Listsupplier(@RequestParam int page) {
        responseData responseData = new responseData();
        try {
            Map<String, Object> customers = iUserInfoService.listSupplier(page);
            responseData.setData(customers);
            responseData.setDesc("Fetch successful");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/searchcustomer")
    public ResponseEntity<?> findByCriteriaCustomer(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        responseData responseData = new responseData();
        try {
            Map<String, Object> customers = iUserInfoService.findByCriteriaCustomer(criteria, query, page);
            responseData.setData(customers);
            responseData.setDesc("Fetch successful");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/searchsupplier")
    public ResponseEntity<?> findByCriteriaSupplier(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        responseData responseData = new responseData();
        try {
            Map<String, Object> suppliers = iUserInfoService.findByCriteriaSupplier(criteria, query, page);
            responseData.setData(suppliers);
            responseData.setDesc("Fetch successful");
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") int id) {
        responseData responseData = new responseData();
        responseData.setData(iUserInfoService.getUserInfo(id).getDTO());
        System.out.println(iUserInfoService.getUserInfo(id).getDTO());
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

}
