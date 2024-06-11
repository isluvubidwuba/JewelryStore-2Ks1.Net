package com.ks1dotnet.jewelrystore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.entity.Employee;
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
        System.out.println("Requested page: " + page);
        ResponseData ResponseData = new ResponseData();
        ResponseData.setStatus(HttpStatus.OK);
        ResponseData.setData(iEmployeeService.getHomePageEmployee(page));
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/listemployee/{id}")
    private ResponseEntity<?> findEmployee(
            @PathVariable String id) {
        ResponseData ResponseData = new ResponseData();
        ResponseData.setStatus(HttpStatus.OK);

        ResponseData.setData(iEmployeeService.listEmployee(id).getDTO());
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
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
        ResponseData responseData = new ResponseData();
        responseData = iEmployeeService.insertEmployee(file, firstName, lastName, pinCode, phoneNumber,
                email,
                address, roleId, status);

        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> updateEmployee(
            @RequestParam MultipartFile file,
            @RequestParam String id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam int roleId,
            @RequestParam String pinCode,
            @RequestParam boolean status,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address) {
        try {
            ResponseData responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName, roleId, pinCode,
                    status, phoneNumber, email, address);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (Exception e) {
            ResponseData errorResponse = new ResponseData();
            errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            errorResponse.setDesc("System Error: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseData> deleteEmployee(@PathVariable String id) {
        ResponseData ResponseData = new ResponseData();
        try {
            Employee employee = iEmployeeService.findById(id);
            employee.setStatus(false);
            Employee updateEmployee = iEmployeeService.save(employee);
            ResponseData.setStatus(HttpStatus.OK);
            ResponseData.setDesc("Delete successfull");
            ResponseData.setData(updateEmployee);
        } catch (Exception e) {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            ResponseData.setDesc("Delete fail. Internal Server Error");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseData, ResponseData.getStatus());
    }

    // @GetMapping("/files/{filename:.+}")
    // @ResponseBody
    // public ResponseEntity<?> getFile(@PathVariable String filename) {
    // Resource resource = iFileService.loadFile(filename);
    // return ResponseEntity.ok()
    // .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
    // resource.getFilename() + "\"")
    // .body(resource);
    // }

    @PostMapping("/search")
    public ResponseEntity<?> searchEmployees(
            @RequestParam("criteria") String criteria,
            @RequestParam("query") String query,
            @RequestParam("page") int page) {
        ResponseData ResponseData = new ResponseData();
        Map<String, Object> response = iEmployeeService.findByCriteria(criteria, query, page);
        ResponseData.setData(response);
        ResponseData.setStatus(HttpStatus.OK);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/getstaff")
    public ResponseEntity<?> getStaff() {
        ResponseData responseData = iEmployeeService.getStaff();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
