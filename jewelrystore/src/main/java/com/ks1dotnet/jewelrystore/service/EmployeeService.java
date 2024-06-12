package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
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

   @Autowired
   private PasswordEncoder passwordEncoder;

   @Value("${fileUpload.userPath}")
   private String filePath;

   @Value("${firebase.img-url}")
   private String url;

   @Autowired
   private FirebaseStorageService firebaseStorageService;

   @Override
   public List<Employee> findAll() {
      try {
         List<Employee> list = iEmployeeRepository.findAll();
         return list;
      } catch (RunTimeExceptionV1 e) {
         throw new RunTimeExceptionV1("Load list Employee Error", e.getMessage());
      }
   }

   @Override
   public Employee save(Employee employee) {
      try {
         Employee emp = iEmployeeRepository.save(employee);
         return emp;
      } catch (RuntimeException e) {
         throw new RunTimeExceptionV1("Can not save employee", e.getMessage());
      }
   }

   @Override
   public Employee findById(String id) {
      return iEmployeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found Employee ID :" + id));
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

      String fileName = null;

      // Upload hình ảnh nếu có
      if (file != null && !file.isEmpty()) {
         responseData = firebaseStorageService.uploadImage(file, filePath);
         if (responseData.getStatus() == HttpStatus.OK) {
            fileName = (String) responseData.getData();
            responseData.setStatus(HttpStatus.OK);
            responseData.setDesc("Find Image Successfully !!!");
         } else {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Upload image error ! System Error !");
            return responseData;
         }
      }
      responseData = firebaseStorageService.uploadImage(file, filePath);
      fileName = "31ab6d6b-86cf-443f-9041-3c394b17ac0b_2024-06-10";
      if (responseData.getStatus() == HttpStatus.OK)
         fileName = (String) responseData.getData();

      // Check if email, phone number, or ID already exists
      if (iEmployeeRepository.existsByEmail(email)) {
         responseData.setStatus(HttpStatus.CONFLICT);
         responseData.setDesc("Email already exists");
         return responseData;
      }

      if (iEmployeeRepository.existsByPhoneNumber(phoneNumber)) {
         responseData.setStatus(HttpStatus.CONFLICT);
         responseData.setDesc("Phone number already exists");
         return responseData;
      }

      // Assuming generateUniqueEmployeeId() checks for uniqueness
      String generatedId = generateUniqueEmployeeId();
      if (iEmployeeRepository.existsById(generatedId)) {
         responseData.setStatus(HttpStatus.CONFLICT);
         responseData.setDesc("Generated ID already exists");
         return responseData;
      }

      Employee employee = new Employee();
      employee.setId(generatedId);
      employee.setFirstName(firstName);
      employee.setLastName(lastName);

      // Mã hóa pinCode
      String encodedPinCode = passwordEncoder.encode(pinCode);
      employee.setPinCode(encodedPinCode);

      employee.setPhoneNumber(phoneNumber);
      employee.setEmail(email);
      employee.setAddress(address);
      employee.setStatus(status);
      employee.setRole(iRoleService.findById(roleId));
      employee.setImage(fileName);
      iEmployeeRepository.save(employee);

      responseData.setStatus(HttpStatus.OK);
      responseData.setDesc("Insert successful");

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
   public ResponseData updateEmployee(MultipartFile file, String id, String firstName, String lastName, int roleId,
         String pinCode, boolean status, String phoneNumber, String email, String address) {
      ResponseData responseData = new ResponseData();
      String fileName = null;

      // Upload hình ảnh nếu có
      if (file != null && !file.isEmpty()) {
         responseData = firebaseStorageService.uploadImage(file, filePath);
         if (responseData.getStatus() == HttpStatus.OK) {
            fileName = (String) responseData.getData();
         } else {
            responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            responseData.setDesc("Upload image error ! System Error !");
            return responseData;
         }
      }

      // Nếu hình ảnh không được upload, lấy hình ảnh cũ
      if (fileName == null) {
         Optional<Employee> existingEmployeeOpt = iEmployeeRepository.findById(id);
         if (existingEmployeeOpt.isPresent()) {
            Employee existingEmployee = existingEmployeeOpt.get();
            fileName = existingEmployee.getImage();
         } else {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Not found employee");
            return responseData;
         }
      }

      // Cập nhật thông tin nhân viên
      Optional<Employee> employeeOpt = iEmployeeRepository.findById(id);
      if (employeeOpt.isPresent()) {
         Employee employee = employeeOpt.get();
         employee.setFirstName(firstName);
         employee.setLastName(lastName);
         employee.setPinCode(pinCode);
         employee.setPhoneNumber(phoneNumber);
         employee.setEmail(email);
         employee.setAddress(address);
         employee.setStatus(status);
         employee.setRole(iRoleService.findById(roleId));
         employee.setImage(fileName);

         iEmployeeRepository.save(employee);
         EmployeeDTO employeeDTO = employee.getDTO();

         responseData.setStatus(HttpStatus.OK);
         responseData.setDesc("Update Successful");
         responseData.setData(employeeDTO);
      } else {
         responseData.setStatus(HttpStatus.NOT_FOUND);
         responseData.setDesc("Not Found Employee");
      }

      return responseData;
   }

   @Override
   public Employee listEmployee(String id) {
      return iEmployeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found Employee ID :" + id));
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
      if (employeePage.hasContent()) {
         for (Employee employee : employeePage) {
            EmployeeDTO empDTO = employee.getDTO();
            if (empDTO.getImage() != null && !empDTO.getImage().isEmpty()) {
               empDTO.setImage(empDTO.getImage().trim());
               System.out.println("Check URL Image in Search Function : " + empDTO.getImage());
            }
            employeeDTOs.add(empDTO);
         }
      }
      response.put("employees", employeeDTOs);
      response.put("totalPages", employeePage.getTotalPages());
      response.put("currentPage", page);
      return response;
   }

   @Override
   public ResponseData getStaff() {
      ResponseData responseData = new ResponseData();
      try {
         List<EmployeeDTO> employeeDTOs = new ArrayList<>();
         for (Employee employee : iEmployeeRepository.findAllStaff()) {
            employeeDTOs.add(employee.getDTO());
         }
         responseData.setDesc("Load list staff successfully");
         responseData.setStatus(HttpStatus.OK);
         responseData.setData(employeeDTOs);
         return responseData;
      } catch (RunTimeExceptionV1 e) {
         throw new RunTimeExceptionV1("Find all staff error", e.getMessage());
      }
   }

}
