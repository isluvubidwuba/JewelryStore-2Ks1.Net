package com.ks1dotnet.jewelrystore.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
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
        ResponseData ResponseData = new ResponseData();
        try {
            List<ExchangeRatePolicyDTO> lExchangeRatePolicyDTOs = iExchangeRatePolicyService.getFullExchange();
            // List<PolicyForInvoiceDTO> listPFIDTO =
            // iPolicyForInvoiceService.findAllPolicyForInvoice();
            ResponseData.setData(lExchangeRatePolicyDTOs);
            ResponseData.setStatus(HttpStatus.OK);

            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving policy for invoice service(method getAllPolicyForInvoiceService): "
                    + e.getMessage());
            ResponseData.setDesc("Failed to retrieve policies.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getExchangeApply")
    public ResponseEntity<?> getAllPolicyByInvoiceId(int invoiceType) {
        ResponseData ResponseData = new ResponseData();
        try {
            return new ResponseEntity<>(
                    iExchangeRatePolicyService.getListExchangeRatePoliciesByInvoiceType(invoiceType),
                    HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving policy for invoice (method getAllPolicyByInvoiceId): "
                    + e.getMessage());
            ResponseData.setDesc("Failed to retrieve policies.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/infor")
    public ResponseEntity<?> inforExchangeRate(@RequestParam String idExchangeRate) {
        ResponseData ResponseData;
        try {
            ResponseData = iExchangeRatePolicyService.getInforByID(idExchangeRate);
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving infor exchange rate: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to retrieve exchange rate infor.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detailExchangeRate(@RequestParam String idExchangeRate) {
        ResponseData ResponseData = new ResponseData();
        try {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("selectOption", iExchangeRatePolicyService.getFullByID(idExchangeRate));
            dataMap.put("fullOption", iInvoiceTypeService.getFullInvoice());

            ResponseData.setData(dataMap);
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving detail exchange rate: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to retrieve exchange rate details.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/applySelectedOptions")
    public ResponseEntity<?> applySelectedOptions(@RequestBody Map<String, Object> payload) {
        ResponseData ResponseData = new ResponseData();
        try {
            String idExchangeRate = (String) payload.get("idExchangeRate"); // idExchangeApply
            @SuppressWarnings("unchecked")
            List<Object> selectedOptionsRaw = (List<Object>) payload.get("selectedOptions");
            List<Integer> selectedOptions = new ArrayList<>();// list InvoiceTye apply

            for (Object obj : selectedOptionsRaw) {
                if (obj instanceof Integer) {
                    selectedOptions.add((Integer) obj);
                } else if (obj instanceof String) {
                    selectedOptions.add(Integer.parseInt((String) obj));
                }
            }
            ResponseData = iExchangeRatePolicyService.applySelectOptions(idExchangeRate, selectedOptions);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving detail exchange rate: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to retrieve exchange rate details.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateexchange")
    public ResponseEntity<?> updateExchange(@RequestParam String idExchange, @RequestParam String desc,
            @RequestParam float rate, @RequestParam boolean status) {
        ResponseData ResponseData;
        try {
            ResponseData = iExchangeRatePolicyService.updateExchangeRatePolicy(idExchange, desc, rate, status);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error updating exchange rate policy: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to update exchange rate policy.");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deleteexchange")
    public ResponseEntity<?> deleteExchange(@RequestParam String idExchange) {
        ResponseData ResponseData;
        try {
            ResponseData = iExchangeRatePolicyService.deleteExchangeRatePolicy(idExchange);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error updating exchange rate policy: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to update exchange rate policy.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createexchange")
    public ResponseEntity<?> createPolicy(@RequestParam String idExchange, @RequestParam String desc,
            @RequestParam float rate, @RequestParam boolean status) {
        ResponseData ResponseData;
        try {
            ResponseData = iExchangeRatePolicyService.createExchangeRatePolicy(idExchange, desc, rate, status);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error creating exchange rate policy: " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to create exchange rate policy.");
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);

            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/createpolicy")
    public ResponseEntity<?> createPolicy(@RequestParam int idInvoiceType, @RequestParam String idExchange) {
        ResponseData ResponseData = iPolicyForInvoiceService.createPolicyForInvoice(idInvoiceType, idExchange);

        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/createinvoicetype")
    public ResponseEntity<?> createInvoiceType(@RequestParam String name) {
        ResponseData ResponseData = iInvoiceTypeService.createInvoiceType(name);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/addinvoicetype")
    public ResponseEntity<?> addInvoiceType(@RequestParam String invoiceType) {
        ResponseData ResponseData;
        try {
            ResponseData = iInvoiceTypeService.addInvoiceType(invoiceType);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error add inovoice type : " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to addd invoice type.");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateinvoicetype")
    public ResponseEntity<?> updateInvoiceType(@RequestParam int idInvoiceType, @RequestParam String invoiceType) {
        ResponseData ResponseData;
        try {
            ResponseData = iInvoiceTypeService.updateInvoice(idInvoiceType, invoiceType);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error update inovoice type : " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to update invoice type.");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/searhExchangeRate")
    public ResponseEntity<?> updateInvoiceType(@RequestParam String keyword) {
        ResponseData ResponseData = new ResponseData();
        try {
            List<ExchangeRatePolicyDTO> lExchangeRatePolicyDTOs = iExchangeRatePolicyService
                    .searchExchangeRate(keyword);
            ResponseData.setData(lExchangeRatePolicyDTOs);
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>(ResponseData, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error update inovoice type : " + e.getMessage());
            ResponseData = new ResponseData();
            ResponseData.setDesc("Failed to update invoice type.");
            return new ResponseEntity<>(ResponseData, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
