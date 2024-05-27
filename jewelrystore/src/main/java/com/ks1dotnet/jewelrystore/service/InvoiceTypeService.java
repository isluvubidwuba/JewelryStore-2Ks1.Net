package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
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

    @Override
    public List<InvoiceTypeDTO> getFullInvoice() {
        List<InvoiceTypeDTO> list = new ArrayList<>();
        try {
            List<InvoiceType> invoiceType = iInvoiceTypeRepository.findAll();
            for (InvoiceType invoiceType2 : invoiceType) {
                list.add(invoiceType2.getDTO());
            }
            return list;
        } catch (Exception e) {
            System.out.println("Throw method get Full Invoice: " + e.getMessage());
        }
        return list;

    }

    @Override
    public responseData addInvoiceType(String invoiceType) {
        responseData responseData = new responseData();
        try {
            InvoiceType invoiceType2 = new InvoiceType(new InvoiceTypeDTO(0, invoiceType));
            responseData.setData(iInvoiceTypeRepository.save(invoiceType2).getDTO());
            responseData.setDesc("Add invoice type successfull!");
            return responseData;
        } catch (Exception e) {
            System.out.println("Throw method add Invoice type: " + e.getMessage());
            responseData.setDesc("Fail to add invoice type");
            return responseData;
        }
    }

    @Override
    public responseData updateInvoice(int idInvoiceType, String invoiceType) {
        responseData responseData = new responseData();
        try {
            Optional<InvoiceType> invoiceType2 = iInvoiceTypeRepository.findById(idInvoiceType);
            if (invoiceType2.isPresent()) {
                InvoiceType invoiceTypeEntity = invoiceType2.get();
                invoiceTypeEntity.setName(invoiceType);
                responseData.setData(iInvoiceTypeRepository.save(invoiceTypeEntity).getDTO());
                responseData.setDesc("update invoice type successfull!");
                return responseData;

            }
            responseData.setData("Fail to update invoice type");
            responseData.setDesc("Update invoice type fail!");
            return responseData;
        } catch (Exception e) {
            System.out.println("Throw method add Invoice type: " + e.getMessage());
            responseData.setDesc("Fail to add invoice type");
            return responseData;
        }
    }

}
