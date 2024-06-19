package com.ks1dotnet.jewelrystore.controller;

import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.MailService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@RestController
@RequestMapping("/mail")
@CrossOrigin("*")
public class MailController {
    @Autowired
    private MailService mailService;

    @Autowired
    private IEmployeeService iEmployeeService;

    // @PostMapping("/send/{mail}")
    // public String sendMail(@PathVariable String mail, @RequestBody MailStructure
    // mailStructure) {
    // mailService.sendMail(mail, mailStructure);
    // return "Successfully sent mail";
    // }

    @PostMapping("/sendInfo/{idEmp}")
    public ResponseEntity<?> sendAccount(@PathVariable String idEmp) {
        Employee emp = iEmployeeService.findById(idEmp);
        ResponseData response = mailService.sendAccountForEmployee(emp.getEmail(), idEmp, idEmp,
                emp.getFirstName() + " " + emp.getLastName());
        return new ResponseEntity<>(response, response.getStatus());
    }

    @PostMapping("/sendOtp/{idEmp}")
    public ResponseEntity<?> sendInvoice(@PathVariable String idEmp) {
        try {
            if (!idEmp.startsWith("SE"))
                throw new ResourceNotFoundException("No employee found " + idEmp);
            Employee emp = iEmployeeService.findById(idEmp);
            if (emp == null) {
                throw new ResourceNotFoundException("No employee found " + idEmp);
            }

            ResponseData response = mailService.sendOtpEmail(emp.getEmail(),
                    emp.getFirstName() + " " + emp.getLastName());

            emp.setOtp(response.getData().toString());
            emp.setOtpGenerDateTime(LocalDateTime.now());
            iEmployeeService.save(emp);

            response.setData(null);
            return new ResponseEntity<>(response, response.getStatus());

        } catch (ResourceNotFoundException e) {
            ResponseData errorResponse =
                    new ResponseData(HttpStatus.NOT_FOUND, e.getMessage(), null);
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            ResponseData errorResponse = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau.", null);
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
