package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.PolicyForInvoiceDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IPolicyForInvoiceService {
    public List<PolicyForInvoiceDTO> findAllPolicyForInvoice();

    public PolicyForInvoiceDTO findById(int id);

    public ResponseData createPolicyForInvoice(int idInvoiceType, String idExchange);
}