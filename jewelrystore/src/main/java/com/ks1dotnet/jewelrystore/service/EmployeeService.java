package com.ks1dotnet.jewelrystore.service;

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
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

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

   @Autowired
   private JwtUtilsHelper jwtUtilsHelper;

   @Override
   // @PostAuthorize("returnObject.data.email == authentication.name")
   public ResponseData myProfile() {
      ResponseData response = new ResponseData();
      try {
         String id = jwtUtilsHelper.getAuthorizationByTokenType("rt").getSubject();
         Employee employee = iEmployeeRepository.findById(id).orElseThrow(
               () -> new ApplicationException("User not exist!", HttpStatus.NOT_FOUND));
         EmployeeDTO emp = employee.getDTO();
         emp.setImage(url.trim() + filePath.trim() + emp.getImage());
         response.setData(emp);
         response.setDesc("My profile");
         response.setStatus(HttpStatus.OK);
      } catch (ApplicationException e) {
         throw new ApplicationException("Error at myProfile EmployeeService: " + e.getMessage(),
               e.getErrorString(), e.getStatus());
      } catch (Exception e) {
         throw new ApplicationException("Error at myProfile EmployeeService: " + e.getMessage(),
               "Erro while get profile");
      }
      return response;
   }

   @Override
   public List<Employee> findAll() {
      try {
         List<Employee> list = iEmployeeRepository.findAll();
         return list;
      } catch (Exception e) {
         throw new ApplicationException("Error at findAll EmployeeService: " + e.getMessage(),
               "Load list employee failed!");
      }
   }

   @Override
   public Employee save(Employee employee) {
      try {
         Employee emp = iEmployeeRepository.save(employee);
         return emp;
      } catch (Exception e) {
         throw new ApplicationException("Error at save EmployeeService: " + e.getMessage(),
               "Can not save employee!");
      }
   }

   @Override
   public Employee findById(String id) {
      return iEmployeeRepository.findById(id).orElseThrow(
            () -> new ApplicationException("Not Found Employee ID :" + id, HttpStatus.NOT_FOUND));
   }

   @Override
   public ResponseData getHomePageEmployee(int page, String id) {
      ResponseData responseData = new ResponseData();
      try {
         Map<String, Object> response = new HashMap<>();
         PageRequest pageRequest = PageRequest.of(page, 10);
         List<EmployeeDTO> employeeDTOs = new ArrayList<>();
         Page<Employee> listData;
         if ("admin".equals(id)) {
            listData = iEmployeeRepository.findAll(pageRequest);
         } else
            listData = iEmployeeRepository.findAllExceptRole(1, pageRequest);
         employeeDTOs = convertToDTOPage(listData).getContent();
         response.put("employees", employeeDTOs);
         response.put("totalPages", listData.getTotalPages());
         response.put("currentPage", page);

         responseData.setStatus(HttpStatus.OK);
         responseData.setData(response);
      } catch (Exception e) {
         throw new ApplicationException(
               "Error at getHomePageEmployee EmployeeService: " + e.getMessage(),
               "Can not load page employee!");
      }
      return responseData;

   }

   @Override
   public ResponseData insertEmployee(MultipartFile file, String firstName, String lastName,
         String phoneNumber, String email, String address, int roleId) {
      ResponseData responseData = new ResponseData();
      try {
         String fileName = null;

         if (file != null && !file.isEmpty()) {
            fileName = firebaseStorageService.uploadImage(file, filePath).getData().toString();
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
         employee.setStatus(true);
         employee.setRole(iRoleService.findById(roleId));
         employee.setImage(fileName);
         iEmployeeRepository.save(employee);
         responseData.setStatus(HttpStatus.OK);
         responseData.setDesc("Insert employee successful");
         responseData.setData(employee.getId());
         return responseData;
      } catch (Exception e) {
         throw new ApplicationException(
               "Error at insertEmployee EmployeeService: " + e.getMessage(),
               "Insert employee failed!!");
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

         // Check if email, phone number, or ID already exists
         if (iEmployeeRepository.existsByEmailExceptId(email, id)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Email already exists");
            return responseData;
         }

         if (iEmployeeRepository.existsByPhoneNumberExceptId(phoneNumber, id)) {
            responseData.setStatus(HttpStatus.CONFLICT);
            responseData.setDesc("Phone number already exists");
            return responseData;
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
         throw new ApplicationException(
               "Error at updateEmployee EmployeeService : " + e.getMessage(),
               "Fail to update employee!!");
      }

   }

   @Override
   public ResponseData listEmployee(String id) {
      Employee employee = iEmployeeRepository.findById(id).orElseThrow(
            () -> new ApplicationException("Not Found Employee ID :" + id, HttpStatus.NOT_FOUND));

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
               employeePage =
                     iEmployeeRepository.findByRoleNameContainingIgnoreCase(query, pageRequest);
               break;

            case "status":
               Boolean status = "active".equalsIgnoreCase(query);
               employeePage = iEmployeeRepository.findByStatus(status, pageRequest);
               break;

            default:
               throw new ApplicationException("Invalid search criteria: " + criteria,
                     "Invalid criteria!");
         }
         if (employeePage.isEmpty()) {
            responseData.setStatus(HttpStatus.NOT_FOUND);
            responseData.setDesc("Employee not exist in system");
         } else {
            Page<EmployeeDTO> employeeDTOPage = convertToDTOPage(employeePage);
            response.put("employees", employeeDTOPage.getContent());
            response.put("totalPages", employeePage.getTotalPages());
            response.put("currentPage", page);

            responseData.setStatus(HttpStatus.OK);
            responseData.setData(response);
            responseData.setDesc("Find employee succesful");
         }
      } catch (ApplicationException e) {
         throw new ApplicationException(
               "Error at findbyCriteria EmployeeService: " + e.getMessage(), e.getErrorString(),
               e.getStatus());
      } catch (Exception e) {
         throw new ApplicationException(
               "Error at findByCriteria EmployeeService: " + e.getMessage(), "invalid criterial!");
      }
      return responseData;
   }

   @Override
   public ResponseData getStaff() {
      ResponseData responseData = new ResponseData();
      try {
         List<EmployeeDTO> employeeDTOs = new ArrayList<>();
         for (Employee employee : iEmployeeRepository.findAllStaffByStatus()) {
            employeeDTOs.add(employee.getDTO());
         }
         responseData.setDesc("Load list staff successfully");
         responseData.setStatus(HttpStatus.OK);
         responseData.setData(employeeDTOs);
         return responseData;
      } catch (Exception e) {
         throw new ApplicationException("Error at getStaff EmployeeService: " + e.getMessage(),
               "Load list staff failed!");
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
         responseData.setDesc("Delete employee successful");
         responseData.setData(updatedEmployee);
      } catch (Exception e) {
         throw new ApplicationException(
               "Error at deleteEmployee EmployeeService: " + e.getMessage(),
               "Delete employee failed!");
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

}
