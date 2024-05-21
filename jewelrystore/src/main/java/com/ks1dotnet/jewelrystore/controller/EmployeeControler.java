package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.PagedEmployeeResponse;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeControler {
    @Autowired
    private IEmployeeService iEmployeeService;
    @Autowired
    private IRoleService iRoleService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        List<Employee> listEmpl = iEmployeeService.findAll();
        Page<Employee> employees = iEmployeeService.getPaginatedEntities(page, size);
        PagedEmployeeResponse pagedEmployeeResponse = new PagedEmployeeResponse(listEmpl, employees);
        responseData responseData = new responseData();
        responseData.setData(pagedEmployeeResponse);
        responseData.setStatus(HttpStatus.OK.value());
        responseData.setDesc("Success");

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PutMapping("/insert")
    public ResponseEntity<String> insertEmployee(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String pinCode,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address,
            @RequestParam int roleId,
            @RequestParam boolean status) {
        try {
            Employee employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setPinCode(pinCode);
            employee.setPhoneNumber(phoneNumber);
            employee.setEmail(email);
            employee.setAddress(address);
            // Assume roleService is a service to fetch the role by ID
            employee.setRole(iRoleService.findById(roleId));
            employee.setStatus(status);
            iEmployeeService.save(employee);
            return new ResponseEntity<>("Employee created successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating employee: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateEmployee(
            @RequestParam int id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam int roleId,
            @RequestParam boolean status,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String address) {
        try {
            Employee employee2 = iEmployeeService.findById(id);
            if (employee2 == null) {
                responseData responseData = new responseData(404, "Employee not found", null);
                return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
            }
            employee2.setFirstName(firstName);
            employee2.setLastName(lastName);
            employee2.setRole(iRoleService.findById(roleId)); // Assume roleService is a service to fetch the role by ID
            employee2.setStatus(status);
            employee2.setPhoneNumber(phoneNumber);
            employee2.setEmail(email);
            employee2.setAddress(address);

            Employee updateEmployee = iEmployeeService.save(employee2);
            responseData responseData = new responseData(201, "Update Employee Successful", updateEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData responseData = new responseData(400, "Error updating employee: " + e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<responseData> deleteEmployee(@PathVariable Integer id) {
        try {
            Employee employee = iEmployeeService.findById(id);
            if (employee == null) {
                responseData responseData = new responseData(404, "Employee not found", null);
                return new ResponseEntity<>(responseData, HttpStatus.NOT_FOUND);
            }

            employee.setStatus(false);
            Employee updatedEmployee = iEmployeeService.save(employee);
            responseData responseData = new responseData(200, "Employee deleted successfully", updatedEmployee);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            responseData responseData = new responseData(400, "Error deleting employee: " + e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        }
    }

}
