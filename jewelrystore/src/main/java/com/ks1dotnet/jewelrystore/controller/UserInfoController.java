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
import com.ks1dotnet.jewelrystore.payload.ResponseData;
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
        ResponseData ResponseData = new ResponseData();
        ResponseData.setData(iUserInfoService.getHomePageUser(page));
        ResponseData.setStatus(HttpStatus.OK);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
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
        ResponseData responseData = iUserInfoService.insertUserInfo(file, fullName, phoneNumber, email, roleId,
                address);

        return new ResponseEntity<>(responseData, responseData.getStatus());

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
        ResponseData ResponseData = new ResponseData();

        UserInfoDTO userInfoDTO = iUserInfoService.updateUserInfo(file, id, fullName, phoneNumber, email, roleId,
                address);
        if (userInfoDTO != null) {
            ResponseData.setDesc("Update successful");
            ResponseData.setData(userInfoDTO);
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } else {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listcustomer")
    public ResponseEntity<?> listCustomer(@RequestParam int page) {
        ResponseData ResponseData = new ResponseData();
        try {
            Map<String, Object> customers = iUserInfoService.listCustomer(page);
            ResponseData.setData(customers);
            ResponseData.setDesc("Fetch successful");
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/listsupplier")
    public ResponseEntity<?> Listsupplier(@RequestParam int page) {
        ResponseData ResponseData = new ResponseData();
        try {
            Map<String, Object> customers = iUserInfoService.listSupplier(page);
            ResponseData.setData(customers);
            ResponseData.setDesc("Fetch successful");
            ResponseData.setStatus(HttpStatus.OK);

            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/searchcustomer")
    public ResponseEntity<?> findByCriteriaCustomer(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        ResponseData ResponseData = new ResponseData();
        try {
            Map<String, Object> customers = iUserInfoService.findByCriteriaCustomer(criteria, query, page);
            ResponseData.setData(customers);
            ResponseData.setStatus(HttpStatus.OK);

            ResponseData.setDesc("Fetch successful");
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/searchsupplier")
    public ResponseEntity<?> findByCriteriaSupplier(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        ResponseData ResponseData = new ResponseData();
        try {
            Map<String, Object> suppliers = iUserInfoService.findByCriteriaSupplier(criteria, query, page);
            ResponseData.setData(suppliers);
            ResponseData.setStatus(HttpStatus.OK);

            ResponseData.setDesc("Fetch successful");
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Fetch failed. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") int id) {
        ResponseData ResponseData = new ResponseData();
        ResponseData.setStatus(HttpStatus.OK);
        ResponseData.setData(iUserInfoService.getUserInfo(id).getDTO());
        System.out.println(iUserInfoService.getUserInfo(id).getDTO());
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

}
