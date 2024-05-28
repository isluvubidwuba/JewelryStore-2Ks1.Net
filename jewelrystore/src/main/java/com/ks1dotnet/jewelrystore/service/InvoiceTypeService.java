package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;

@Service
public class InvoiceTypeService implements IInvoiceTypeService {
    @Autowired
    IInvoiceTypeRepository iInvoiceTypeRepository;

    @Override
    public ResponseData createInvoiceType(String name) {
        ResponseData ResponseData = new ResponseData();
        try {
            InvoiceType save = new InvoiceType();
            save.setName(name);
            InvoiceType invoiceType = iInvoiceTypeRepository.save(save);
            ResponseData.setData(invoiceType.getDTO());
            ResponseData.setDesc("Create invoice type success");
        } catch (Exception e) {
            ResponseData.setDesc("An error occurred: " + e.getMessage());
        }
        return ResponseData;
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
    public ResponseData addInvoiceType(String invoiceType) {
        ResponseData ResponseData = new ResponseData();
        try {
            InvoiceType invoiceType2 = new InvoiceType(new InvoiceTypeDTO(0, invoiceType));
            ResponseData.setData(iInvoiceTypeRepository.save(invoiceType2).getDTO());
            ResponseData.setDesc("Add invoice type successfull!");
            return ResponseData;
        } catch (Exception e) {
            System.out.println("Throw method add Invoice type: " + e.getMessage());
            ResponseData.setDesc("Fail to add invoice type");
            return ResponseData;
        }
    }

    @Override
    public ResponseData updateInvoice(int idInvoiceType, String invoiceType) {
        ResponseData ResponseData = new ResponseData();
        try {
            Optional<InvoiceType> invoiceType2 = iInvoiceTypeRepository.findById(idInvoiceType);
            if (invoiceType2.isPresent()) {
                InvoiceType invoiceTypeEntity = invoiceType2.get();
                invoiceTypeEntity.setName(invoiceType);
                ResponseData.setData(iInvoiceTypeRepository.save(invoiceTypeEntity).getDTO());
                ResponseData.setDesc("update invoice type successfull!");
                return ResponseData;

            }
            ResponseData.setData("Fail to update invoice type");
            ResponseData.setDesc("Update invoice type fail!");
            return ResponseData;
        } catch (Exception e) {
            System.out.println("Throw method add Invoice type: " + e.getMessage());
            ResponseData.setDesc("Fail to add invoice type");
            return ResponseData;
        }
    }

}
