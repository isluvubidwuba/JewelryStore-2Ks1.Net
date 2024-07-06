package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;

@RestController
@RequestMapping("/invoice-type")
@CrossOrigin("*")
public class InvoiceTypeController {

    @Autowired
    private IInvoiceTypeService invoiceTypeService;

    @GetMapping
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

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getInvoiceTypeById(@PathVariable int id) {

        try {
            ResponseData responseData = invoiceTypeService.getInvoiceTypeById(id);
            return new ResponseEntity<>(responseData, responseData.getStatus());
        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at getInvoiceTypeById InvoiceTypeController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at getInvoiceTypeById InvoiceTypeController: " + e.getMessage(),
                    "Something wrong while get invoice type by id!");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createInvoiceType(@RequestParam String name) {

        try {
            if (name.trim().isEmpty()) {
                throw new ApplicationException("Cannot insert null into name",
                        HttpStatus.BAD_REQUEST);
            }
            ResponseData responseData = invoiceTypeService.createInvoiceType(name);
            return new ResponseEntity<>(responseData, responseData.getStatus());

        } catch (ApplicationException e) {
            throw new ApplicationException(
                    "Error at createInvoiceType InvoiceTypeController: " + e.getMessage(),
                    e.getErrorString(), e.getStatus());
        } catch (Exception e) {
            throw new ApplicationException(
                    "Error at createInvoiceType InvoiceTypeController: " + e.getMessage(),
                    "Something wrong while create invoice type!");
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ResponseData> updateInvoiceType(@PathVariable int id,
            @RequestParam String name) {

        try {
            if (name.trim().isEmpty()) {
                throw new ApplicationException("Cannot insert null into name",
                        HttpStatus.BAD_REQUEST);
            }
            ResponseData responseData = invoiceTypeService.updateInvoiceType(id, name);
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
