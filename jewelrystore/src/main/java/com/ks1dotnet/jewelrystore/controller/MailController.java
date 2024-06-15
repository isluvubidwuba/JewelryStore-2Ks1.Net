package com.ks1dotnet.jewelrystore.controller;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.MailStructure;
import com.ks1dotnet.jewelrystore.entity.Employee;
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

    @PostMapping("/send/{idEmp}")
    public ResponseEntity<?> sendAccount(@PathVariable String idEmp) {
        Employee emp = iEmployeeService.findById(idEmp);
        ResponseData response = mailService.sendAccountForEmployee(emp.getEmail(), idEmp, idEmp,
                emp.getFirstName() + " " + emp.getLastName());
        return new ResponseEntity<>(response, response.getStatus());
    }

}
