package com.ks1dotnet.jewelrystore.controller;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.MailService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@RestController
@RequestMapping("${apiURL}/mail")
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

    @PostMapping("/sendInvoice")
    public ResponseEntity<?> sendInvoice(@RequestParam String email, @RequestParam String userName,
            @RequestParam int invoiceID) {

        try {
            ResponseData response = mailService.sendInvoiceEmail(email, userName, invoiceID);
            return new ResponseEntity<>(response, response.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    "Something wrong while sending invoice to customer " + userName + "!");
        }
    }

    @PostMapping("/sendOtp/{idEmp}")
    public ResponseEntity<?> sendInvoice(@PathVariable String idEmp) {
        try {
            if (!idEmp.startsWith("SE"))
                throw new ApplicationException("No employee found with id: " + idEmp,
                        HttpStatus.NOT_FOUND);
            Employee emp = iEmployeeService.findById(idEmp);
            if (emp == null) {
                throw new ApplicationException("No employee found: " + idEmp, HttpStatus.NOT_FOUND);
            }

            ResponseData response = mailService.sendOtpEmail(emp.getEmail(),
                    emp.getFirstName() + " " + emp.getLastName());

            emp.setOtp(response.getData().toString());
            emp.setOtpGenerDateTime(LocalDateTime.now());
            iEmployeeService.save(emp);

            response.setData(null);
            return new ResponseEntity<>(response, response.getStatus());

        } catch (ApplicationException e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException("Error at sendInvoice MailController: " + e.getMessage(),
                    "Something wrong while sending otp to employee have id: " + idEmp + " !");
        }
    }

}
