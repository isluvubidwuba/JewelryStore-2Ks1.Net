package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.FirebaseStorageService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IUserInfoService;

@RestController
@RequestMapping("/userinfo")
@CrossOrigin("*")
public class UserInfoController {

    @Autowired
    private IUserInfoService iUserInfoService;

    @Value("${fileUpload.userPath}")
    private String filePath;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @PostMapping("/insert")
    public ResponseEntity<?> insertUserInfo(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam int roleId,
            @RequestParam String address) {

        ResponseData responseData = iUserInfoService.insertUserInfo(file, fullName, phoneNumber, email, roleId,
                address);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam int id,
            @RequestParam String fullName,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam int roleId,
            @RequestParam String address) {
        ResponseData responseData = iUserInfoService.updateUserInfo(file, id, fullName, phoneNumber, email, roleId,
                address);
        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @GetMapping("/listcustomer")
    public ResponseEntity<?> listCustomer(@RequestParam int page) {
        ResponseData responseData = iUserInfoService.listCustomer(page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/listsupplier")
    public ResponseEntity<?> Listsupplier(@RequestParam int page) {
        ResponseData responseData = iUserInfoService.listSupplier(page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/searchcustomer")
    public ResponseEntity<?> findByCriteriaCustomer(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        ResponseData responseData = iUserInfoService.findByCriteriaCustomer(criteria, query, page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/searchsupplier")
    public ResponseEntity<?> findByCriteriaSupplier(@RequestParam String criteria, @RequestParam String query,
            @RequestParam int page) {
        ResponseData responseData = iUserInfoService.findByCriteriaSupplier(criteria, query, page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/findcustomer/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") int id) {
        ResponseData ResponseData = iUserInfoService.getUserInfo(id);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("Received file upload request for file: " + file.getOriginalFilename());
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/findsupplier/{id}")
    public ResponseEntity<?> getSupplierById(@PathVariable int id) {
        ResponseData responseData = iUserInfoService.getSupplierInfo(id);
        return new ResponseEntity<>(responseData.getData(), responseData.getStatus());
    }

    @GetMapping("/getcustomer/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable int id) {
        ResponseData responseData = iUserInfoService.getCustomerInfo(id);
        return new ResponseEntity<>(responseData.getData(), responseData.getStatus());
    }

    @GetMapping("/phonenumberandmailcustomer")
    public ResponseEntity<?> getPhoneNumberCustomer(@RequestParam String phone) {
        if (phone.contains("@")) {
            ResponseData responseData = iUserInfoService.findByEmail(phone);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } else {
            ResponseData responseData = iUserInfoService.findByPhoneNumber(phone);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        }

    }

    @GetMapping("/phonenumberandmailsupplier")
    public ResponseEntity<?> getPhoneNumberSupplier(@RequestParam String citeria) {
        if (citeria.contains("@")) {
            ResponseData responseData = iUserInfoService.findByEmailSupplier(citeria);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } else {
            ResponseData responseData = iUserInfoService.findByPhoneSupplier(citeria);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        }

    }

}
