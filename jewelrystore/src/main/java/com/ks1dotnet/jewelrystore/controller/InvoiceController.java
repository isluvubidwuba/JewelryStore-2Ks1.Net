package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;
import com.ks1dotnet.jewelrystore.service.InvoiceService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/create-detail")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            InvoiceDetailDTO invoiceDetailDTO = invoiceService.createInvoiceDetail(
                    request.getBarcode(), request.getInvoiceTypeId(), request.getQuantity());

            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice created successfully",
                    invoiceDetailDTO);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
