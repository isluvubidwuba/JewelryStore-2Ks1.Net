package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
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
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.exception.RunTimeExceptionV1;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.FirebaseStorageService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

@RestController
@RequestMapping("/employee")
@CrossOrigin("*")
public class EmployeeController {

    @Value("${fileUpload.userPath}")
    private String filePath;

    @Autowired
    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private IEmployeeService iEmployeeService;

    @Autowired
    private JwtUtilsHelper jwtUtilsHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/listpage")
    private ResponseEntity<?> getHomePageEmployee(@RequestParam int page) {
        ResponseData responseData = iEmployeeService.getHomePageEmployee(page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/listemployee/{id}")
    private ResponseEntity<?> findEmployee(@PathVariable String id) {
        ResponseData responseData = iEmployeeService.listEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertEmployee(@RequestParam(required = false) MultipartFile file,
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String pinCode, @RequestParam String phoneNumber,
            @RequestParam String email, @RequestParam String address, @RequestParam int roleId,
            @RequestParam boolean status) {
        ResponseData responseData = iEmployeeService.insertEmployee(file, firstName, lastName,
                pinCode, phoneNumber, email, address, roleId, status);

        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/update")
    public ResponseEntity<ResponseData> updateEmployee(@RequestHeader("Authorization") String token,
            @RequestParam(required = false) MultipartFile file, @RequestParam String id,
            @RequestParam String firstName, @RequestParam String lastName, @RequestParam(required = false) int roleId,
            @RequestParam(required = false) String pinCode, @RequestParam boolean status,
            @RequestParam String phoneNumber, @RequestParam(required = false) String email,
            @RequestParam String address) {
        ResponseData responseData;
        String idEmployeeFromToken = jwtUtilsHelper.getEmployeeIdFromToken(
                token.substring(5));
        EmployeeDTO employeeDTO = iEmployeeService.findById(idEmployeeFromToken).getDTO();
        String pincodeCheck = pinCode != null ? pinCode : employeeDTO.getPinCode();
        String mailCheck = email != null ? email : employeeDTO.getEmail();
        if (employeeDTO.getRole().getName().equals("ADMIN")) {
            responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName,
                    roleId, pincodeCheck, status, phoneNumber, mailCheck, address);
        } else if (employeeDTO.getId().equals(id)) {
            responseData = iEmployeeService.updateEmployee(file, id, firstName, lastName,
                    employeeDTO.getRole().getId(), pincodeCheck, status, phoneNumber, mailCheck, address);
        } else {
            responseData = new ResponseData(HttpStatus.BAD_REQUEST, "Not have permission", null);
        }

        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        ResponseData response = firebaseStorageService.uploadImage(file, filePath);
        return new ResponseEntity<>(response, response.getStatus());
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<ResponseData> deleteEmployee(@PathVariable String id) {
        ResponseData responseData = iEmployeeService.deleteEmployee(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());

    }

    @PostMapping("/search")
    public ResponseEntity<?> searchEmployees(@RequestParam("criteria") String criteria,
            @RequestParam("query") String query, @RequestParam("page") int page) {
        ResponseData responseData = iEmployeeService.findByCriteria(criteria, query, page);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/changePass")
    public ResponseEntity<?> changePass(@RequestParam String pwd, @RequestParam String token,
            @RequestParam String idEmploy) {
        ResponseData responseData = new ResponseData();
        if (!idEmploy.startsWith("SE"))
            throw new ResourceNotFoundException("No employee found " + idEmploy);
        Employee em = iEmployeeService.findById(idEmploy);
        if (jwtUtilsHelper.verifyToken(token, em.getId() + em.getOtp())) {

            em.setPinCode(passwordEncoder.encode(pwd));
            iEmployeeService.save(em);
            responseData = new ResponseData(HttpStatus.OK, "Change password successfully", null);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        }
        responseData = new ResponseData(HttpStatus.UNAUTHORIZED, "Change password failed", null);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/validateOtp")
    public ResponseEntity<?> validateOtp(@RequestParam("otp") String otp,
            @RequestParam String idEmployee) {
        if (!idEmployee.startsWith("SE"))
            throw new ResourceNotFoundException("No employee found " + idEmployee);
        if (otp.isEmpty())
            throw new RunTimeExceptionV1("Otp can not be empty");
        ResponseData responseData = iEmployeeService.validateOtp(otp, idEmployee);
        if (responseData.getStatus() != HttpStatus.OK)
            return new ResponseEntity<>(responseData, responseData.getStatus());
        String token = jwtUtilsHelper.generateToken(idEmployee + otp);
        responseData.setData(token);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/getstaff")
    public ResponseEntity<?> getStaff() {
        ResponseData responseData = iEmployeeService.getStaff();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

}
