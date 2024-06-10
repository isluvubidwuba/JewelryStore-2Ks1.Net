package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IExchangeRatePolicyService;

@RestController
@RequestMapping("/exchange-rate-policy")
@CrossOrigin("*")
public class ExchangeRatePolicyController {

    @Autowired
    private IExchangeRatePolicyService exchangeRatePolicyService;

    @GetMapping
    public ResponseEntity<ResponseData> getAllExchangeRatePolicies() {

        ResponseData responseData = exchangeRatePolicyService.getAllExchangeRatePolicies();
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseData> getExchangeRatePolicyById(@PathVariable String id) {

        ResponseData responseData = exchangeRatePolicyService.getExchangeRatePolicyById(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @GetMapping("/by-invoice-type/{invoiceTypeId}")
    public ResponseEntity<ResponseData> getAllExchangeRatePoliciesByInvoiceTypeId(@PathVariable int invoiceTypeId) {

        ResponseData responseData = exchangeRatePolicyService.getAllExchangeRatePoliciesByInvoiceTypeId(invoiceTypeId);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseData> createExchangeRatePolicy(@RequestParam String idExchange,
            @RequestParam String desc,
            @RequestParam Float rate, @RequestParam boolean status,
            @RequestParam Integer invoiceType) {

        ResponseData responseData = exchangeRatePolicyService.createExchangeRatePolicy(idExchange, desc, rate,
                status, invoiceType);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ResponseData> updateExchangeRatePolicy(@PathVariable String id,
            @RequestParam String desc,
            @RequestParam Float rate, @RequestParam boolean status,
            @RequestParam Integer invoiceType) {

        ResponseData responseData = exchangeRatePolicyService.updateExchangeRatePolicy(id, desc, rate,
                status, invoiceType);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }

    @PostMapping("/deactivate/{id}")
    public ResponseEntity<ResponseData> deactivateExchangeRatePolicy(@PathVariable String id) {

        ResponseData responseData = exchangeRatePolicyService.deactivateExchangeRatePolicy(id);
        return new ResponseEntity<>(responseData, responseData.getStatus());
    }
}
