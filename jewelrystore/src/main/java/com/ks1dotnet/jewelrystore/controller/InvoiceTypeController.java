package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;

@RestController
@RequestMapping("${apiURL}/invoice-type")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
public class InvoiceTypeController {

    @Autowired
    private IInvoiceTypeService invoiceTypeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> getAllInvoiceTypes() {

        try {
            ResponseData responseData = invoiceTypeService.getAllInvoiceTypes();
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getAllInvoiceTypes InvoiceTypeController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getAllInvoiceTypes InvoiceTypeController: " + e.getMessage(),
                    "Something wrong while get all invoice types!");
        }
    }

    // @GetMapping("/{id}")
    // public ResponseEntity<ResponseData> getInvoiceTypeById(@PathVariable int id)
    // {
    // ResponseData responseData = invoiceTypeService.getInvoiceTypeById(id);
    // return new ResponseEntity<>(responseData, responseData.getStatus());
    // }

    // @PostMapping("/create")
    // public ResponseEntity<ResponseData> createInvoiceType(@RequestParam String
    // name) {
    // if (name.trim().isEmpty()) {
    // throw new BadRequestException("cannot insert null into name");
    // }
    // ResponseData responseData = invoiceTypeService.createInvoiceType(name);
    // return new ResponseEntity<>(responseData, responseData.getStatus());
    // }

    @GetMapping("/update")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ResponseData> updateInvoiceType(@RequestParam int id,
            @RequestParam float rate) {

        try {
            ResponseData responseData = invoiceTypeService.updateInvoiceType(id, rate);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at updateInvoiceType InvoiceTypeController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at updateInvoiceType InvoiceTypeController: " + e.getMessage(),
                    "Something wrong while update invoice type!");
        }
    }
}
