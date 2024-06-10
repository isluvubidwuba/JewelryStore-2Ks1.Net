package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ks1dotnet.jewelrystore.exception.BadRequestException;
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
        ResponseData responseData = invoiceTypeService.getAllInvoiceTypes();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getInvoiceTypeById(@PathVariable int id) {
        ResponseData responseData = invoiceTypeService.getInvoiceTypeById(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createInvoiceType(@RequestParam String name) {
        if (name.trim().isEmpty()) {
            throw new BadRequestException("cannot insert null into name");
        }
        ResponseData responseData = invoiceTypeService.createInvoiceType(name);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ResponseData> updateInvoiceType(@PathVariable int id, @RequestParam String name) {
        if (name.trim().isEmpty()) {
            throw new BadRequestException("cannot update null into name");
        }
        ResponseData responseData = invoiceTypeService.updateInvoiceType(id, name);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }
}
