package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;

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

        ResponseData responseData = new ResponseData();
        responseData = iUserInfoService.insertUserInfo(file, fullName, phoneNumber, email, roleId,
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
        ResponseData responseData = new ResponseData();
        responseData = iUserInfoService.updateUserInfo(file, id, fullName, phoneNumber, email, roleId,
                address);
        if (responseData.getStatus() == HttpStatus.OK) {
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(responseData, responseData.getStatus());
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

    @GetMapping("/findcustomer/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable("id") int id) {
        ResponseData ResponseData = new ResponseData();
        ResponseData.setStatus(HttpStatus.OK);
        ResponseData.setData(iUserInfoService.getUserInfo(id).getDTO());
        System.out.println(iUserInfoService.getUserInfo(id).getDTO());
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        System.out.println("Received file upload request for file: " + file.getOriginalFilename());
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/uploadget")
    public ResponseEntity<?> getImageUrl(@RequestParam("fileName") String fileName) {
        System.out.println("Received request for file: " + fileName);
        try {
            String fileUrl = firebaseStorageService.getFileUrl(fileName);
            System.out.println("File URL: " + fileUrl);
            ResponseData response = new ResponseData(HttpStatus.OK, "Get image URL successfully", fileUrl);
            return new ResponseEntity<>(response, response.getStatus());
        } catch (Exception e) {
            System.out.println("Error while getting image URL: " + e.getMessage());
            ResponseData response = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to get image URL", null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
