package com.ks1dotnet.jewelrystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;

@Service
public class InvoiceTypeService implements IInvoiceTypeService {
    @Autowired
    IInvoiceTypeRepository iInvoiceTypeRepository;

    @Override
    public responseData createInvoiceType(String name) {
        responseData responseData = new responseData();
        try {
            InvoiceType save = new InvoiceType();
            save.setName(name);
            InvoiceType invoiceType = iInvoiceTypeRepository.save(save);
            responseData.setData(invoiceType.getDTO());
            responseData.setDesc("Create invoice type success");
        } catch (Exception e) {
            responseData.setDesc("An error occurred: " + e.getMessage());
        }
        return responseData;
    }

}
