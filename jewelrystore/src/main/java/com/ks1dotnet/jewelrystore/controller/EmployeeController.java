package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.MailService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("${apiURL}/employee")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class EmployeeController {

    @Value("${fileUpload.userPath}")
    private String filePath;

    @Autowired
    private IEmployeeService iEmployeeService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    @GetMapping("/listpage")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> getHomePageEmployee(@RequestParam int page) {
        Claims ATTokenClaims = JwtUtilsHelper.getAuthorizationByTokenType("at");
        ResponseData responseData = iEmployeeService.getHomePageEmployee(page, ATTokenClaims.getSubject());
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/myProfile")
    public ResponseEntity<?> getMyProfile() {
        ResponseData responseData = iEmployeeService.myProfile();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/listemployee/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> findEmployee(@PathVariable String id) {
        ResponseData responseData = iEmployeeService.listEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/insert")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> insertEmployee(@RequestParam(required = false) MultipartFile file,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String phoneNumber, @RequestParam String email,
            @RequestParam String address, @RequestParam int roleId, @RequestParam boolean status) {
        ResponseData responseData = iEmployeeService.insertEmployee(file, firstName, lastName,
                phoneNumber, email, address, roleId);

        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> updateEmployee(
            @RequestParam(required = false) MultipartFile file, @RequestParam String id,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam(required = false) Integer roleId, // Đổi int thành Integer
            @RequestParam(required = false) String pinCode,
            @RequestParam(required = false) Boolean status, // Đổi boolean thành Boolean
            @RequestParam String phoneNumber, @RequestParam(required = false) String email,
            @RequestParam String address) {
        ResponseData responseData;
        String idEmployeeFromToken = SecurityContextHolder.getContext().getAuthentication().getName();
        EmployeeDTO employeeDTO = iEmployeeService.findById(idEmployeeFromToken).getDTO();
        String pincodeCheck = pinCode != null ? pinCode : employeeDTO.getPinCode();
        String mailCheck = email != null ? email : employeeDTO.getEmail();

        if (employeeDTO.getRole().getName().equals("ADMIN")) {
            responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName, roleId,
                    pincodeCheck, status, phoneNumber, mailCheck, address);
        } else if (employeeDTO.getId().equals(id)) {
            responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName,
                    employeeDTO.getRole().getId(), pincodeCheck, employeeDTO.isStatus(),
                    phoneNumber, mailCheck, address);
        } else {
            responseData = new ResponseData(HttpStatus.BAD_REQUEST, "Not have permission", null);
        }

        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/update2")
    public ResponseEntity<ResponseData> updateEmployee2(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String address) {
        ResponseData responseData;
        String idEmp = JwtUtilsHelper.getAuthorizationByTokenType("at").getSubject();
        System.out.println(idEmp);
        System.out.println(id);
        Employee employeeDTO = iEmployeeService.findById(idEmp);
        // Sử dụng dữ liệu cũ nếu dữ liệu mới không có hoặc rỗng
        String firstNameToUpdate = firstName != null ? (firstName.isEmpty() ? employeeDTO.getFirstName() : firstName)
                : employeeDTO.getFirstName();
        String lastNameToUpdate = lastName != null ? (lastName.isEmpty() ? employeeDTO.getLastName() : lastName)
                : employeeDTO.getLastName();
        String phoneNumberToUpdate = phoneNumber != null
                ? (phoneNumber.isEmpty() ? employeeDTO.getPhoneNumber() : phoneNumber)
                : employeeDTO.getPhoneNumber();
        String emailToUpdate = email != null ? (email.isEmpty() ? employeeDTO.getEmail() : email)
                : employeeDTO.getEmail();
        String addressToUpdate = address != null ? (address.isEmpty() ? employeeDTO.getAddress() : address)
                : employeeDTO.getAddress();

        if (employeeDTO.getId().equals(id)) {
            responseData = iEmployeeService.updateEmployee(file, idEmp,
                    firstNameToUpdate, lastNameToUpdate, employeeDTO.getRole().getId(),
                    employeeDTO.getPinCode(), employeeDTO.isStatus(), phoneNumberToUpdate,
                    emailToUpdate, addressToUpdate);
        } else {
            responseData = new ResponseData(HttpStatus.BAD_REQUEST, "Not have permission", null);
        }

        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> deleteEmployee(@PathVariable String id) {
        ResponseData responseData = iEmployeeService.deleteEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> searchEmployees(@RequestParam("criteria") String criteria,
            @RequestParam("query") String query, @RequestParam("page") int page) {
        ResponseData responseData = iEmployeeService.findByCriteria(criteria, query, page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/changePass")
    public ResponseEntity<?> changePass(@RequestParam String pwd) {
        try {
            ResponseData responseData = new ResponseData();
            Claims claim = JwtUtilsHelper.getAuthorizationByTokenType("at");
            String idEmploy = claim.getSubject();
            if (!idEmploy.startsWith("SE"))
                throw new ApplicationException("No employee found " + idEmploy,
                        HttpStatus.NOT_FOUND);
            Employee em = iEmployeeService.findById(idEmploy);
            em.setPinCode(passwordEncoder.encode(pwd));
            iEmployeeService.save(em);
            responseData = new ResponseData(HttpStatus.OK, "Change password successfully", null);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at changePass EmployeeController: " + e.getMessage(), e.getErrorString(),
                    e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at changePass EmployeeController: " + e.getMessage(),
                    "Change password failed");
        }
    }

    @GetMapping("/getstaff")
    @PreAuthorize("hasAnyAuthority('ADMIN','MANAGER')")
    public ResponseEntity<?> getStaff() {
        ResponseData responseData = iEmployeeService.getStaff();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/sendInfo/{idEmp}")
    public ResponseEntity<?> sendAccount(@PathVariable String idEmp) {
        try {
            Employee emp = iEmployeeService.findById(idEmp);
            ResponseData response = mailService.sendAccountForEmployee(emp.getEmail(), idEmp, idEmp,
                    emp.getFirstName() + " " + emp.getLastName());
            return new ResponseEntity<>(response, response.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at sendAccount MailController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at sendAccount MailController: " + e.getMessage(),
                    "Something wrong while sending acccount to employee have id: " + idEmp + "!");
        }
    }

}
