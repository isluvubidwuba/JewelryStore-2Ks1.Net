package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeControler {

    @Value("${fileUpload.userPath}")
    private String filePath;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private IEmployeeService iEmployeeService;

    @GetMapping("/listpage")
    private ResponseEntity<?> getHomePageEmployee(
            @RequestParam int page) {
        ResponseData responseData = iEmployeeService.getHomePageEmployee(page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/listemployee/{id}")
    private ResponseEntity<?> findEmployee(
            @PathVariable String id) {
        ResponseData responseData = iEmployeeService.listEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertEmployee(
            @RequestParam MultipartFile file,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String pinCode,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam int roleId,
            @RequestParam boolean status) {
        ResponseData responseData = iEmployeeService.insertEmployee(file, firstName, lastName, pinCode, phoneNumber,
                email,
                address, roleId, status);

        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> updateEmployee(
            @RequestParam(required = false) MultipartFile file,
            @RequestParam String id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam int roleId,
            @RequestParam String pinCode,
            @RequestParam boolean status,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address) {
        ResponseData responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName, roleId, pinCode,
                status, phoneNumber, email, address);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseData> deleteEmployee(@PathVariable String id) {
        ResponseData responseData = iEmployeeService.deleteEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/search")
    public ResponseEntity<?> searchEmployees(
            @RequestParam("criteria") String criteria,
            @RequestParam("query") String query,
            @RequestParam("page") int page) {
        ResponseData responseData = iEmployeeService.findByCriteria(criteria, query, page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/getstaff")
    public ResponseEntity<?> getStaff() {
        ResponseData responseData = iEmployeeService.getStaff();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
