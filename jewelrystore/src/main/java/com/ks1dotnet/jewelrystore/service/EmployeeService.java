package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@Service
public class EmployeeService implements IEmployeeService {
   @Autowired
   private IEmployeeRepository iEmployeeRepository;
   @Autowired
   private IRoleService iRoleService;

   @Override
   public List<Employee> findAll() {
      return iEmployeeRepository.findAll();
   }

   @Override
   public Employee save(Employee employee) {
      return iEmployeeRepository.save(employee);
   }

   @Override
   public Employee findById(String id) {
      return iEmployeeRepository.findById(id).orElse(null);
   }

   @Override
   public Map<String, Object> getHomePageEmployee(int page) {
      Map<String, Object> response = new HashMap<>();
      PageRequest pageRequest = PageRequest.of(page, 5);
      List<EmployeeDTO> employeeDTOs = new ArrayList<>();
      Page<Employee> listData = iEmployeeRepository.findAll(pageRequest);

      for (Employee e : listData) {
         employeeDTOs.add(e.getDTO());
      }
      response.put("employees", employeeDTOs);
      response.put("totalPages", listData.getTotalPages());
      response.put("currentPage", page);
      return response;
   }

   @Override
   public ResponseData insertEmployee(MultipartFile file, String firstName, String lastName, String pinCode,
         String phoneNumber, String email, String address, int roleId, boolean status) {
      ResponseData responseData = new ResponseData();

      // Check if email, phone number, or ID already exists
      // if (iEmployeeRepository.existsByEmail(email)) {
      // responseData.setStatus(HttpStatus.CONFLICT);
      // responseData.setDesc("Email already exists");
      // return responseData;
      // }

      // if (iEmployeeRepository.existsByPhoneNumber(phoneNumber)) {
      // responseData.setStatus(HttpStatus.CONFLICT);
      // responseData.setDesc("Phone number already exists");
      // return responseData;
      // }

      // // Assuming generateUniqueEmployeeId() checks for uniqueness
      // String generatedId = generateUniqueEmployeeId();
      // if (iEmployeeRepository.existsById(generatedId)) {
      // responseData.setStatus(HttpStatus.CONFLICT);
      // responseData.setDesc("Generated ID already exists");
      // return responseData;
      // }

      // boolean isSaveFileSuccess = true;
      // String imageName;
      // // Check if a file is provided
      // // if (file != null && !file.isEmpty()) {
      // // try {
      // // isSaveFileSuccess = iFileService.savefile(file);
      // // if (isSaveFileSuccess) {
      // // imageName = file.getOriginalFilename();
      // // } else {
      // // responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
      // // responseData.setDesc("File save failed");
      // // return responseData;
      // // }
      // // } catch (Exception e) {
      // // responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
      // // responseData.setDesc("File save failed: " + e.getMessage());
      // // return responseData;
      // // }
      // // } else {
      // // imageName = "default_image.png";
      // // }

      // Employee employee = new Employee();
      // employee.setId(generatedId);
      // employee.setFirstName(firstName);
      // employee.setLastName(lastName);
      // employee.setPinCode(pinCode);
      // employee.setPhoneNumber(phoneNumber);
      // employee.setEmail(email);
      // employee.setAddress(address);
      // employee.setStatus(status);
      // employee.setRole(iRoleService.findById(roleId));
      // employee.setImage(imageName);
      // iEmployeeRepository.save(employee);

      // responseData.setStatus(HttpStatus.OK);
      // responseData.setDesc("Insert successful");

      return responseData;
   }

   public String generateUniqueEmployeeId() {
      String idPrefix = "SE";
      String uniqueId;
      do {
         StringBuilder sb = new StringBuilder();
         sb.append(idPrefix);
         for (int i = 0; i < 8; i++) {
            int randomDigit = (int) (Math.random() * 10);
            sb.append(randomDigit);
         }
         uniqueId = sb.toString();
      } while (iEmployeeRepository.existsById(uniqueId)); // Giả sử bạn có phương thức kiểm tra ID trùng lặp

      return uniqueId;
   }

   @Override
   public EmployeeDTO updateEmployee(MultipartFile file, String id, String firstName, String lastName, int roleId,
         String pinCode, boolean status, String phoneNumber, String email, String address) {
      // boolean isSaveFileSuccess = iFileService.savefile(file);
      // Optional<Employee> employee = iEmployeeRepository.findById(id);
      // System.out.println(employee);
      // EmployeeDTO employeeDTO = new EmployeeDTO();
      // if (employee.isPresent()) {
      // Employee employee1 = new Employee();
      // employee1.setId(id);
      // employee1.setFirstName(firstName);
      // employee1.setLastName(lastName);
      // employee1.setPinCode(pinCode);
      // employee1.setPhoneNumber(phoneNumber);
      // employee1.setEmail(email);
      // employee1.setAddress(address);
      // employee1.setStatus(status);
      // employee1.setRole(iRoleService.findById(roleId));
      // if (isSaveFileSuccess) {
      // employee1.setImage(file.getOriginalFilename());
      // } else {
      // employee1.setImage(employee.get().getImage());
      // }
      // iEmployeeRepository.save(employee1);
      // employeeDTO = employee1.getDTO();
      // }
      // return employeeDTO;
      return null;
   }

   @Override
   public Employee listEmployee(String id) {
      return iEmployeeRepository.findById(id).orElse(null);
   }

   @Override
   public Map<String, Object> findByCriteria(String criteria, String query, int page) {
      Map<String, Object> response = new HashMap<>();
      PageRequest pageRequest = PageRequest.of(page, 5); // Size cố định là 5
      Page<Employee> employeePage;

      switch (criteria.toLowerCase()) {
         case "id":
            Optional<Employee> employeeOpt = iEmployeeRepository.findById(query);
            if (employeeOpt.isPresent()) {
               List<Employee> employeeList = Collections.singletonList(employeeOpt.get());
               employeePage = new PageImpl<>(employeeList, pageRequest, 1);
            } else {
               employeePage = new PageImpl<>(Collections.emptyList(), pageRequest, 0);
            }
            break;

         case "name":
            employeePage = iEmployeeRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query,
                  query, pageRequest);
            break;

         case "role":
            employeePage = iEmployeeRepository.findByRoleNameContainingIgnoreCase(query, pageRequest);
            break;

         case "status":
            Boolean status = "active".equalsIgnoreCase(query) ? true : false;
            employeePage = iEmployeeRepository.findByStatus(status, pageRequest);
            break;

         default:
            throw new IllegalArgumentException("Invalid search criteria: " + criteria);
      }

      List<EmployeeDTO> employeeDTOs = new ArrayList<>();
      for (Employee employee : employeePage) {
         employeeDTOs.add(employee.getDTO());
      }

      response.put("employees", employeeDTOs);
      response.put("totalPages", employeePage.getTotalPages());
      response.put("currentPage", page);
      return response;
   }

}
