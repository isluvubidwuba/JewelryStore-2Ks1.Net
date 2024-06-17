package com.ks1dotnet.jewelrystore.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.entity.Invoice;
import com.ks1dotnet.jewelrystore.exception.BadRequestException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceDetailRequest;
import com.ks1dotnet.jewelrystore.payload.request.InvoiceRequest;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceService;

@RestController
@RequestMapping("/invoice")
@CrossOrigin("*")
public class InvoiceController {
    @Autowired
    private IInvoiceService invoiceService;
    @Autowired
    private IInvoiceRepository invoiceRepository;

    @PostMapping("/create-detail")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceDetailRequest request) {
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

    @PostMapping("/create-invoice")
    public ResponseEntity<ResponseData> createInvoice(@RequestBody InvoiceRequest request) {
        try {
            HashMap<String, Integer> barcodeQuantity = new HashMap<>();
            for (Map.Entry<String, String> entry : request.getBarcodeQuantityMap().entrySet()) {
                barcodeQuantity.put(entry.getKey(), Integer.parseInt(entry.getValue()));
            }

            int isSuccessCreateInvoice = invoiceService.createInvoiceFromDetails(barcodeQuantity,
                    request.getInvoiceTypeId(),
                    request.getUserId(), request.getEmployeeId(), request.getPayment(), request.getNote());

            ResponseData responseData = new ResponseData(HttpStatus.OK, "Invoice created successfully",
                    isSuccessCreateInvoice);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/view-invoice")
    public ResponseEntity<ResponseData> viewInvoice(@RequestParam int invoice) {
        try {
            Invoice Invoice = invoiceRepository.findById(invoice)
                    .orElseThrow(() -> new BadRequestException("Not found invoice with id: " + invoice));

            return new ResponseEntity<>(new ResponseData(HttpStatus.OK, "Get invoice successfull", Invoice.getDTO()),
                    HttpStatus.OK);
        } catch (BadRequestException e) {
            ResponseData responseData = new ResponseData(HttpStatus.BAD_REQUEST, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            ResponseData responseData = new ResponseData(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), null);
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
