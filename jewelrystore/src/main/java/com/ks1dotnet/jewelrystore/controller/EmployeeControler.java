package com.ks1dotnet.jewelrystore.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
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

        ResponseData responseData = iEmployeeService.insertEmployee(file, firstName, lastName, pinCode, phoneNumber,
                email,
                address, roleId, status);

        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(
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
        ResponseData responseData = new ResponseData();

        EmployeeDTO employeeDTO = iEmployeeService.updateEmployee(file, id, firstName, lastName,
                roleId, pinCode, status, phoneNumber, email, address);
        if (employeeDTO != null) {
            responseData.setStatus(HttpStatus.OK);

            responseData.setDesc("Update successful");
            responseData.setData(employeeDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } else {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Update failed. Internal Server Error");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
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
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        Resource resource = iFileService.loadFile(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

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

}
