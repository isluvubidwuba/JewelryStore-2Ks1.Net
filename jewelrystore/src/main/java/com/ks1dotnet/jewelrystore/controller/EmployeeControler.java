package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeControler {
    @Autowired
    private IEmployeeService iEmployeeService;

    @Autowired
    private IFileService iFileService;

    @GetMapping("/listpage")
    private ResponseEntity<?> getHomePageEmployee(
            @RequestParam int page) {
        System.out.println("Requested page: " + page);
        responseData responseData = new responseData();
        responseData.setData(iEmployeeService.getHomePageEmployee(page));
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/listemployee/{id}")
    private ResponseEntity<?> findEmployee(
            @PathVariable int id) {
        responseData responseData = new responseData();
        responseData.setData(iEmployeeService.listEmployee(id).getDTO());
        return new ResponseEntity<>(responseData, HttpStatus.OK);
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

        responseData responseData = new responseData();
        boolean isSuccess = iEmployeeService.insertEmployee(file, firstName, lastName, pinCode, phoneNumber, email,
                address, roleId, status);

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
    public ResponseEntity<?> updateEmployee(
            @RequestParam MultipartFile file,
            @RequestParam int id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam int roleId,
            @RequestParam boolean status,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address) {
        responseData responseData = new responseData();

        EmployeeDTO employeeDTO = iEmployeeService.updateEmployee(file, id, firstName, lastName, lastName, phoneNumber,
                email, address, status, roleId);
        if (employeeDTO != null) {
            responseData.setDesc("Update successful");
            responseData.setData(employeeDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(500);
            responseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<responseData> deleteEmployee(@PathVariable Integer id) {
        responseData responseData = new responseData();
        try {
            Employee employee = iEmployeeService.findById(id);
            employee.setStatus(false);
            Employee updateEmployee = iEmployeeService.save(employee);
            responseData.setDesc("Delete successfull");
            responseData.setData(updateEmployee);
        } catch (Exception e) {
            responseData.setStatus(500);
            responseData.setDesc("Delete fail. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        Resource resource = iFileService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}
