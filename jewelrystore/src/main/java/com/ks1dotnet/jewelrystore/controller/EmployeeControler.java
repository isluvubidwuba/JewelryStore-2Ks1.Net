package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeControler {
    @Autowired
    private IEmployeeService iEmployeeService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        responseData responseData = new responseData();
        List<Employee> listEmpl = iEmployeeService.findAll();
        responseData.setData(listEmpl);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertEmployee(@RequestBody Employee employee) {
        try {
            Employee savedEmployee = iEmployeeService.save(employee);
            responseData responseData = new responseData(201, "Employee created successfully", savedEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData responseData = new responseData(400, "Error creating employee: " + e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee employee) {
        try {
            Employee employee2 = iEmployeeService.findById(employee.getId());
            if (employee2 == null) {
                responseData responseData = new responseData(404, "Employee not found", null);
                return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
            }
            employee2.setFirstName(employee.getFirstName());
            employee2.setLastName(employee.getLastName());
            employee2.setRole(employee.getRole());
            employee2.setStatus(employee.isStatus());

            Employee updateEmployee = iEmployeeService.save(employee2);
            responseData responseData = new responseData(201, "Update Employee Successful", updateEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData responseData = new responseData(400, "Error updating employee: " + e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<responseData> deleteEmployee(@PathVariable Integer id) {
        try {
            Employee employee = iEmployeeService.findById(id);
            if (employee == null) {
                responseData responseData = new responseData(404, "Employee not found", null);
                return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
            }

            employee.setStatus(false);
            Employee updatedEmployee = iEmployeeService.save(employee);
            responseData responseData = new responseData(200, "Employee deleted successfully (status set to false)", updatedEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData responseData = new responseData(400, "Error deleting employee: " + e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }


}
