package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.PolicyForInvoiceDTO;
import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.PolicyForInvoice;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IExchangeRatePolicyRepository;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.repository.IPolicyForInvoiceRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IPolicyForInvoiceService;

@Service
public class PolicyForInvoiceService implements IPolicyForInvoiceService {
    @Autowired
    private IPolicyForInvoiceRepository iPolicyForInvoiceRepository;
    @Autowired
    IExchangeRatePolicyRepository iExchangeRatePolicyRepository;
    @Autowired
    IInvoiceTypeRepository iInvoiceTypeRepository;

    @Override
    public List<PolicyForInvoiceDTO> findAllPolicyForInvoice() {
        List<PolicyForInvoiceDTO> listPFIdto = new ArrayList<>();
        List<PolicyForInvoice> listPFI = iPolicyForInvoiceRepository.findAll();
        for (PolicyForInvoice policyForInvoice : listPFI) {
            PolicyForInvoiceDTO pfiDTO = new PolicyForInvoiceDTO();
            pfiDTO.setId(policyForInvoice.getId());
            pfiDTO.setInvoiceTypeDTO(policyForInvoice.getInvoiceType().getDTO());
            pfiDTO.setExchangeRatePolicyDTO(policyForInvoice.getExchangeRatePolicy().getDTO());
            listPFIdto.add(pfiDTO);
        }
        return listPFIdto;
    }

    @Override
    public PolicyForInvoiceDTO findById(int id) {
        return iPolicyForInvoiceRepository.findById(id).orElse(null).getDTO();
    }

    @Override
    public ResponseData createPolicyForInvoice(int idInvoiceType, String idExchange) {
        ResponseData ResponseData = new ResponseData();
        PolicyForInvoice policyForInvoice = new PolicyForInvoice();

        Optional<ExchangeRatePolicy> exchangeRatePolicy = iExchangeRatePolicyRepository.findById(idExchange);
        Optional<InvoiceType> invoiceType = iInvoiceTypeRepository.findById(idInvoiceType);

        if (exchangeRatePolicy.isPresent() && invoiceType.isPresent()) {
            policyForInvoice.setInvoiceType(invoiceType.get());
            policyForInvoice.setExchangeRatePolicy(exchangeRatePolicy.get());
            PolicyForInvoice policyForInvoice2 = iPolicyForInvoiceRepository.save(policyForInvoice);
            if (policyForInvoice2 != null) {
                ResponseData.setData(policyForInvoice2.getDTO());
                ResponseData.setDesc("create Policy For Invoice success");
                return ResponseData;
            }
        }
        ResponseData.setData("create Policy For Invoice fail");
        ResponseData.setDesc("create Policy For Invoice fail");
        return ResponseData;
    }

}
