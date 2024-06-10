package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;
import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.exception.ResourceNotFoundException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IExchangeRatePolicyRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IExchangeRatePolicyService;

@Service
public class ExchangeRatePolicyService implements IExchangeRatePolicyService {

        @Autowired
        private IExchangeRatePolicyRepository exchangeRatePolicyRepository;

        @Autowired
        private IInvoiceTypeRepository invoiceTypeRepository;

        @Override
        public ResponseData getAllExchangeRatePolicies() {
                List<ExchangeRatePolicyDTO> exchangeRatePolicyDTOs = exchangeRatePolicyRepository.findAll().stream()
                                .map(ExchangeRatePolicy::getDTO)
                                .collect(Collectors.toList());
                return new ResponseData(HttpStatus.OK, "Fetched all exchange rate policies", exchangeRatePolicyDTOs);
        }

        @Override
        public ResponseData getExchangeRatePolicyById(String id) {
                ExchangeRatePolicy exchangeRatePolicy = exchangeRatePolicyRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Exchange rate policy not found with id: " + id));
                return new ResponseData(HttpStatus.OK, "Fetched exchange rate policy by id",
                                exchangeRatePolicy.getDTO());
        }

        @Override
        public ResponseData createExchangeRatePolicy(String id, String descriptionPolicy, Float rate, boolean status,
                        Integer invoiceTypeId) {
                InvoiceType invoiceType = invoiceTypeRepository.findById(invoiceTypeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Invoice type not found with id: " + invoiceTypeId));

                ExchangeRatePolicy exchangeRatePolicy = new ExchangeRatePolicy();
                exchangeRatePolicy.setId(id);
                exchangeRatePolicy.setDescription_policy(descriptionPolicy);
                exchangeRatePolicy.setRate(rate);
                exchangeRatePolicy.setStatus(status);
                exchangeRatePolicy.setLastModified();
                exchangeRatePolicy.setInvoiceType(invoiceType);

                ExchangeRatePolicy savedExchangeRatePolicy = exchangeRatePolicyRepository.save(exchangeRatePolicy);
                return new ResponseData(HttpStatus.CREATED, "Created exchange rate policy",
                                savedExchangeRatePolicy.getDTO());
        }

        @Override
        public ResponseData updateExchangeRatePolicy(String id, String descriptionPolicy, Float rate, boolean status,
                        Integer invoiceTypeId) {
                ExchangeRatePolicy exchangeRatePolicy = exchangeRatePolicyRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Exchange rate policy not found with id: " + id));
                InvoiceType invoiceType = invoiceTypeRepository.findById(invoiceTypeId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Invoice type not found with id: " + invoiceTypeId));

                exchangeRatePolicy.setDescription_policy(descriptionPolicy);
                exchangeRatePolicy.setRate(rate);
                exchangeRatePolicy.setStatus(status);
                exchangeRatePolicy.setLastModified();
                exchangeRatePolicy.setInvoiceType(invoiceType);

                ExchangeRatePolicy updatedExchangeRatePolicy = exchangeRatePolicyRepository.save(exchangeRatePolicy);
                return new ResponseData(HttpStatus.OK, "Updated exchange rate policy",
                                updatedExchangeRatePolicy.getDTO());
        }

        @Override
        public ResponseData deactivateExchangeRatePolicy(String id) {
                if (!exchangeRatePolicyRepository.existsById(id)) {
                        throw new ResourceNotFoundException("Exchange rate policy not found with id: " + id);
                }
                exchangeRatePolicyRepository.deactivatePolicyById(id);
                return new ResponseData(HttpStatus.OK, "Deactivated exchange rate policy", null);
        }

        @Override
        public ResponseData getAllExchangeRatePoliciesByInvoiceTypeId(int invoiceTypeId) {
                List<ExchangeRatePolicy> exchangeRatePolicies = exchangeRatePolicyRepository
                                .findAllByInvoiceTypeId(invoiceTypeId);
                List<ExchangeRatePolicyDTO> exchangeRatePolicyDTOs = exchangeRatePolicies.stream()
                                .map(ExchangeRatePolicy::getDTO)
                                .collect(Collectors.toList());
                return new ResponseData(HttpStatus.OK, "Fetched all exchange rate policies by invoice type id",
                                exchangeRatePolicyDTOs);
        }
}
