package com.ks1dotnet.jewelrystore.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@Service
public class EmployeeService implements IEmployeeService {
   @Autowired
   private IEmployeeRepository iEmployeeRepository;

   @Autowired
   private IRoleService iRoleService;
   @Autowired
   private IInvoiceRepository iInvoiceRepository;
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
   public ResponseData getHomePageEmployee(int page) {
      ResponseData responseData = new ResponseData();
      try {
         Map<String, Object> response = new HashMap<>();
         PageRequest pageRequest = PageRequest.of(page, 5);
         List<EmployeeDTO> employeeDTOs = new ArrayList<>();
         Page<Employee> listData = iEmployeeRepository.findAll(pageRequest);
         employeeDTOs = convertToDTOPage(listData).getContent();
         response.put("employees", employeeDTOs);
         response.put("totalPages", listData.getTotalPages());
         response.put("currentPage", page);

         responseData.setStatus(HttpStatus.OK);
         responseData.setData(response);
      } catch (Exception e) {
         responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
         responseData.setDesc("Can Not Loaded Page Employee: " + e.getMessage());
      }
      return responseData;

   }

   @Override
   public ResponseData insertEmployee(MultipartFile file, String firstName, String lastName,
         String pinCode, String phoneNumber, String email, String address, int roleId,
         boolean status) {
      ResponseData responseData = new ResponseData();
      try {
         String fileName = null;

         // Upload hình ảnh nếu có
         responseData = firebaseStorageService.uploadImage(file, filePath);
         if (responseData.getStatus() == HttpStatus.OK) {
            fileName = (String) responseData.getData();
         } else {
            fileName = "3428f415-1df9-499d-93d3-1ce159b12c06_2024-06-12";
         }

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
         String encodedPinCode = passwordEncoder.encode(employee.getId());
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
         responseData.setData(employee.getId());
         return responseData;
      } catch (Exception e) {
         throw new RunTimeExceptionV1("Insert Employee Fail", e.getMessage());
      }

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
      } while (iEmployeeRepository.existsById(uniqueId)); // Giả sử bạn có phương thức kiểm tra ID
                                                          // trùng lặp

      return uniqueId;
   }

   @Override
   public ResponseData updateEmployee(MultipartFile file, String id, String firstName,
         String lastName, int roleId, String pinCode, boolean status, String phoneNumber,
         String email, String address) {

      try {
         ResponseData responseData = new ResponseData();
         // Xử lý hìnha ảnh
         String fileName = null;
         if (file != null && !file.isEmpty()) {
            fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
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

            employee.setImage(fileName == null ? employee.getImage() : fileName);

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
      } catch (Exception e) {
         throw new RunTimeExceptionV1("Fail To Update Employee", e.getMessage());
      }

   }

   @Override
   public ResponseData listEmployee(String id) {
      Employee employee = iEmployeeRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Not Found Employee ID :" + id));

      employee.setImage(url.trim() + filePath.trim() + employee.getImage());

      ResponseData responseData = new ResponseData();
      responseData.setStatus(HttpStatus.OK);
      responseData.setData(employee.getDTO());
      return responseData;

   }

   @Override
   public ResponseData findByCriteria(String criteria, String query, int page) {
      ResponseData responseData = new ResponseData();
      try {
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
               employeePage = iEmployeeRepository
                     .findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(query,
                           query, pageRequest);
               break;

            case "role":
               employeePage = iEmployeeRepository.findByRoleNameContainingIgnoreCase(query, pageRequest);
               break;

            case "status":
               Boolean status = "active".equalsIgnoreCase(query);
               employeePage = iEmployeeRepository.findByStatus(status, pageRequest);
               break;

            default:
               throw new IllegalArgumentException("Invalid search criteria: " + criteria);
         }

         Page<EmployeeDTO> employeeDTOPage = convertToDTOPage(employeePage);
         response.put("employees", employeeDTOPage.getContent());
         response.put("totalPages", employeePage.getTotalPages());
         response.put("currentPage", page);

         responseData.setStatus(HttpStatus.OK);
         responseData.setData(response);
      } catch (Exception e) {
         responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
         responseData.setDesc("Error during search: " + e.getMessage());
      }
      return responseData;
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

   @Override
   public ResponseData deleteEmployee(String id) {
      ResponseData responseData = new ResponseData();
      try {
         Employee employee = findById(id); // Giả sử bạn đã có phương thức findById trong service
         employee.setStatus(false);
         Employee updatedEmployee = save(employee); // Giả sử bạn đã có phương thức save trong
                                                    // service
         responseData.setStatus(HttpStatus.OK);
         responseData.setDesc("Delete successful");
         responseData.setData(updatedEmployee);
      } catch (Exception e) {
         responseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
         responseData.setDesc("Delete failed. Internal Server Error");
      }
      return responseData;

   }

   private Page<EmployeeDTO> convertToDTOPage(Page<Employee> empPage) {
      List<EmployeeDTO> dtolist = empPage.getContent().stream().map(employee -> {
         EmployeeDTO dto = employee.getDTO();
         dto.setImage(url.trim() + filePath.trim() + dto.getImage());
         dto.setTotalRevenue(iInvoiceRepository.sumTotalPriceByEmployeeId(dto.getId()) == null ? 0
               : iInvoiceRepository.sumTotalPriceByEmployeeId(dto.getId()));
         return dto;
      }).collect(Collectors.toList());
      return new PageImpl<>(dtolist, empPage.getPageable(), empPage.getTotalElements());
   }

   @Override
   public ResponseData validateOtp(String otp, String idEmployee) {
      Employee em = iEmployeeRepository.findById(idEmployee).orElse(null);
      if (em == null)
         return new ResponseData(HttpStatus.NOT_FOUND, "Not found employee with id " + idEmployee,
               null);
      if (em.getOtp().equals(otp) && Duration.between(em.getOtpGenerDateTime(), LocalDateTime.now())
            .getSeconds() > (10 * 60)) {
         return new ResponseData(HttpStatus.REQUEST_TIMEOUT, "OTP code is timeout ", null);
      }
      if (!em.getOtp().equals(otp))
         return new ResponseData(HttpStatus.BAD_REQUEST, "OTP code is not correct ", null);
      return new ResponseData(HttpStatus.OK, "OK ", null);
   }

}
