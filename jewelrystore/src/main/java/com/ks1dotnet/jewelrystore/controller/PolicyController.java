package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IExchangeRatePolicyService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPolicyForInvoiceService;

@RestController
@RequestMapping("/policy")
@CrossOrigin("*")
public class PolicyController {
    @Autowired
    private IPolicyForInvoiceService iPolicyForInvoiceService;
    @Autowired
    private IExchangeRatePolicyService iExchangeRatePolicyService;

    @Autowired
    private IInvoiceTypeService iInvoiceTypeService;

    @GetMapping("/listpolicy")
    public ResponseEntity<?> getAllPolicyForInvoiceService() {
        responseData responseData = new responseData();
        try {
            List<ExchangeRatePolicyDTO> lExchangeRatePolicyDTOs = iExchangeRatePolicyService.getFullExchange();
            // List<PolicyForInvoiceDTO> listPFIDTO =
            // iPolicyForInvoiceService.findAllPolicyForInvoice();
            responseData.setData(lExchangeRatePolicyDTOs);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving policy for invoice service(method getAllPolicyForInvoiceService): "
                    + e.getMessage());
            responseData.setDesc("Failed to retrieve policies.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/infor")
    public ResponseEntity<?> inforExchangeRate(@RequestParam String idExchangeRate) {
        responseData responseData;
        try {
            responseData = iExchangeRatePolicyService.getInforByID(idExchangeRate);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving infor exchange rate: " + e.getMessage());
            responseData = new responseData();
            responseData.setDesc("Failed to retrieve exchange rate infor.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detailExchangeRate(@RequestParam String idExchangeRate) {
        responseData responseData;
        try {
            responseData = iExchangeRatePolicyService.getFullByID(idExchangeRate);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving detail exchange rate: " + e.getMessage());
            responseData = new responseData();
            responseData.setDesc("Failed to retrieve exchange rate details.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateexchange")
    public ResponseEntity<?> updateExchange(@RequestParam String idExchange, @RequestParam String desc,
            @RequestParam float rate, @RequestParam boolean status) {
        responseData responseData;
        try {
            responseData = iExchangeRatePolicyService.updateExchangeRatePolicy(idExchange, desc, rate, status);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error updating exchange rate policy: " + e.getMessage());
            responseData = new responseData();
            responseData.setDesc("Failed to update exchange rate policy.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteexchange")
    public ResponseEntity<?> deleteExchange(@RequestParam String idExchange) {
        responseData responseData;
        try {
            responseData = iExchangeRatePolicyService.deleteExchangeRatePolicy(idExchange);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error updating exchange rate policy: " + e.getMessage());
            responseData = new responseData();
            responseData.setDesc("Failed to update exchange rate policy.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createexchange")
    public ResponseEntity<?> createPolicy(@RequestParam String idExchange, @RequestParam String desc,
            @RequestParam float rate, @RequestParam boolean status) {
        responseData responseData;
        try {
            responseData = iExchangeRatePolicyService.createExchangeRatePolicy(idExchange, desc, rate, status);
            return new ResponseEntity<>(responseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error creating exchange rate policy: " + e.getMessage());
            responseData = new responseData();
            responseData.setDesc("Failed to create exchange rate policy.");
            return new ResponseEntity<>(responseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createpolicy")
    public ResponseEntity<?> createPolicy(@RequestParam int idInvoiceType, @RequestParam String idExchange) {
        responseData responseData = iPolicyForInvoiceService.createPolicyForInvoice(idInvoiceType, idExchange);

        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

    @PostMapping("/createinvoicetype")
    public ResponseEntity<?> createInvoiceType(@RequestParam String name) {
        responseData responseData = iInvoiceTypeService.createInvoiceType(name);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }

}
