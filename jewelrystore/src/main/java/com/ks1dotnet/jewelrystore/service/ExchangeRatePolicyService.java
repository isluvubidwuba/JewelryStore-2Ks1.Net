package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.PolicyForInvoice;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IExchangeRatePolicyRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPolicyForInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IExchangeRatePolicyService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPolicyForInvoiceService;

@Service
public class ExchangeRatePolicyService implements IExchangeRatePolicyService {
    @Autowired
    private IExchangeRatePolicyRepository iExchangeRatePolicyRepository;
    @Autowired
    private IPolicyForInvoiceRepository iPolicyForInvoiceRepository;
    @Autowired
    private IInvoiceTypeRepository iInvoiceTypeRepository;
    @Autowired
    private IPolicyForInvoiceService iPolicyForInvoiceService;

    @Override
    public ResponseData createExchangeRatePolicy(String idExchange, String desc, float rate, boolean status) {
        ResponseData ResponseData = new ResponseData();
        try {
            ExchangeRatePolicy newPolicy = new ExchangeRatePolicy();
            newPolicy.setId(idExchange);
            newPolicy.setDescription_policy(desc);
            newPolicy.setRate(rate);
            newPolicy.setStatus(status);
            newPolicy.setLastModified(); // Set last modified to current date
            ExchangeRatePolicyDTO exReturn = iExchangeRatePolicyRepository.save(newPolicy).getDTO();
            ResponseData.setData(exReturn);
            ResponseData.setDesc("Create successful");
        } catch (Exception e) {
            System.err.println("Error creating exchange rate policy: " + e.getMessage());
            ResponseData.setDesc("Failed to create exchange rate policy.");
        }
        return ResponseData;
    }

    @Override
    public List<ExchangeRatePolicyDTO> getFullExchange() {
        List<ExchangeRatePolicyDTO> listDTO = new ArrayList<>();
        try {
            List<ExchangeRatePolicy> list = iExchangeRatePolicyRepository.findAll();
            for (ExchangeRatePolicy exchangeRatePolicy : list) {
                listDTO.add(exchangeRatePolicy.getDTO());
            }
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving exchange rate policies: " + e.getMessage());
            // You can also rethrow the exception or handle it accordingly
        }
        return listDTO;
    }

    @Override
    public List<InvoiceTypeDTO> getFullByID(String idExchangeRate) {
        List<InvoiceTypeDTO> list = new ArrayList<>();
        try {
            List<PolicyForInvoice> listPFI = iPolicyForInvoiceRepository.findAll();
            for (PolicyForInvoice policyForInvoice : listPFI) {
                if (policyForInvoice.getExchangeRatePolicy().getId().equals(idExchangeRate)) {
                    list.add(policyForInvoice.getInvoiceType().getDTO());
                }
            }
            return list;
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err
                    .println("Error retrieving invoices by exchange rate ID (method: getFullByID): " + e.getMessage());
        }
        return list;
    }

    @Override
    public ResponseData updateExchangeRatePolicy(String idExchange, String desc, float rate, boolean status) {
        ResponseData ResponseData = new ResponseData();
        try {
            Optional<ExchangeRatePolicy> exchangeRatePolicyOptional = iExchangeRatePolicyRepository
                    .findById(idExchange);
            if (!exchangeRatePolicyOptional.isPresent()) {
                ResponseData.setData("Exchange rate policy not found");
                ResponseData.setDesc("Update failed");
                return ResponseData;
            }
            ExchangeRatePolicy exPolicy = exchangeRatePolicyOptional.get();
            exPolicy.setDescription_policy(desc);
            exPolicy.setRate(rate);
            exPolicy.setStatus(status);
            exPolicy.setLastModified(); // Set last modified to current date
            ExchangeRatePolicyDTO exReturn = iExchangeRatePolicyRepository.save(exPolicy).getDTO();
            ResponseData.setData(exReturn);
            ResponseData.setDesc("Update successful");
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error updating exchange rate policy: " + e.getMessage());
            ResponseData.setDesc("Failed to update exchange rate policy.");
        }
        return ResponseData;
    }

    @Override
    public ResponseData deleteExchangeRatePolicy(String idExchange) {
        ResponseData ResponseData = new ResponseData();
        try {
            Optional<ExchangeRatePolicy> exchangeRatePolicyOptional = iExchangeRatePolicyRepository
                    .findById(idExchange);
            if (!exchangeRatePolicyOptional.isPresent()) {
                ResponseData.setData("Exchange rate policy not found");
                ResponseData.setDesc("Delete failed");
                return ResponseData;
            }
            ExchangeRatePolicy exPolicy = exchangeRatePolicyOptional.get();
            exPolicy.setStatus(false);
            exPolicy.setLastModified(); // Set last modified to current date
            ExchangeRatePolicyDTO exReturn = iExchangeRatePolicyRepository.save(exPolicy).getDTO();
            ResponseData.setData(exReturn);
            ResponseData.setDesc("Delete successful");
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error Deleting exchange rate policy: " + e.getMessage());
            ResponseData.setDesc("Failed to update exchange rate policy.");
        }
        return ResponseData;
    }

    @Override
    public ResponseData getInforByID(String idExchangeRate) {
        ResponseData ResponseData = new ResponseData();
        try {
            Optional<ExchangeRatePolicy> exchangeRatePolicy = iExchangeRatePolicyRepository.findById(idExchangeRate);
            if (exchangeRatePolicy.isPresent()) {
                ResponseData.setData(exchangeRatePolicy.get().getDTO());
                ResponseData.setDesc("Get invoice by id exchange success");
                return ResponseData;
            }
            ResponseData.setData("Error method get IN4 by ID");
            ResponseData.setDesc("Get invoice by id exchange success");

        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err
                    .println("Error retrieving invoices by exchange rate ID (method: getFullByID): " + e.getMessage());
            ResponseData.setDesc("Failed to get invoices by id exchange rate.");
        }
        return ResponseData;
    }

    @Override
    public ResponseData applySelectOptions(String idExchangeRate, List<Integer> selectedOptions) {
        ResponseData response = new ResponseData();
        try {
            List<InvoiceTypeDTO> listDTOBefore = getFullByID(idExchangeRate);

            // Determine items to add and remove
            List<Integer> currentIds = listDTOBefore.stream()
                    .map(InvoiceTypeDTO::getId)
                    .collect(Collectors.toList());
            List<Integer> idsToAdd = new ArrayList<>(selectedOptions);
            idsToAdd.removeAll(currentIds); // These are the items that need to be added
            List<Integer> idsToRemove = new ArrayList<>(currentIds);
            idsToRemove.removeAll(selectedOptions); // These are the items that need to be removed

            // Add and remove items
            addInvoiceTypes(idExchangeRate, idsToAdd);
            removePolicyForInvoiceTypes(idExchangeRate, idsToRemove);

            response.setDesc("Options applied successfully");
        } catch (Exception e) {
            System.err.println("Error applying selected options: " + e.getMessage());
            response.setDesc("Failed to apply selected options.");
            throw new RuntimeException("Error applying selected options: " + e.getMessage());
        }
        return response;
    }

    private void removePolicyForInvoiceTypes(String idExchangeRate, List<Integer> idsToRemove) {
        try {
            Optional<ExchangeRatePolicy> exchangeOpt = iExchangeRatePolicyRepository.findById(idExchangeRate);
            if (exchangeOpt.isPresent()) {
                ExchangeRatePolicy exchange = exchangeOpt.get();
                for (Integer id : idsToRemove) {
                    Optional<InvoiceType> invoiceTypeOpt = iInvoiceTypeRepository.findById(id);
                    if (invoiceTypeOpt.isPresent()) {
                        InvoiceType invoiceType = invoiceTypeOpt.get();
                        iPolicyForInvoiceRepository.deleteByExchangeRatePolicyAndInvoiceType(exchange.getId(),
                                invoiceType.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error removing invoice types: " + e.getMessage());
            throw new RuntimeException("Error removing invoice types: " + e.getMessage());
        }
    }

    public void addInvoiceTypes(String idExchangeRate, List<Integer> idsToAdd) {
        try {
            for (Integer id : idsToAdd) {
                iPolicyForInvoiceService.createPolicyForInvoice(id, idExchangeRate);
            }
        } catch (Exception e) {
            System.err.println("Error adding invoice types: " + e.getMessage());
            throw new RuntimeException("Error adding invoice types: " + e.getMessage());
        }
    }

    @Override
    public List<ExchangeRatePolicyDTO> searchExchangeRate(String keyword) {
        List<ExchangeRatePolicyDTO> listDTO = new ArrayList<>();
        try {
            List<ExchangeRatePolicy> list = iExchangeRatePolicyRepository.searchByKeyword(keyword);
            for (ExchangeRatePolicy exchangeRatePolicy : list) {
                listDTO.add(exchangeRatePolicy.getDTO());
            }
        } catch (Exception e) {
            // Log the exception (consider using a logging framework)
            System.err.println("Error retrieving exchange rate policies: " + e.getMessage());
            throw new RuntimeException("Error searchExchangeRate: " + e.getMessage());
        }
        return listDTO;
    }

}
