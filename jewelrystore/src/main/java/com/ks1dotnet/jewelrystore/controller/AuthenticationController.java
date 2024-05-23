// package com.ks1dotnet.jewelrystore.controller;

// import java.util.List;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.ks1dotnet.jewelrystore.entity.Employee;
// import com.ks1dotnet.jewelrystore.payload.ResponseData;
// import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;

// @RestController
// @RequestMapping("/authentication")
// @CrossOrigin("*")
// public class AuthenticationController {
// @Autowired
// private IAuthenticationService iAuthenticationService;

// @GetMapping("/list")
// private ResponseEntity<?> findAll() {
// ResponseData responseData = new ResponseData();
// List<Employee> listEmpl = iAuthenticationService.findAll();
// responseData.setData(listEmpl);
// // for (employee e : listEmpl) {
// // System.out.println(e);
// System.out.println(iAuthenticationService.findByPinCode("12"));
// // }
// return new ResponseEntity<>(responseData, HttpStatus.OK);
// }

// @PostMapping("/signup")
// private ResponseEntity<?> login(@RequestBody Employee emp) {
// ResponseData responseData = new ResponseData();
// try {
// Employee employee = iAuthenticationService.findById(emp.getId());

// if (employee == null) {
// responseData.setDesc("SignUp fail. Not found employee");
// } else if (employee.getPinCode().equals(emp.getPinCode())) {
// responseData.setDesc("SignUp successful");
// } else {
// responseData.setDesc("SignUp fail. Pincode Error");
// }
// } catch (Exception e) {
// ResponseData.setStatus(500);
// ResponseData.setDesc("SignUp fail. Internal Server Error");
// return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
// }
// return new ResponseEntity<>(responseData, HttpStatus.OK);
// }
// }
