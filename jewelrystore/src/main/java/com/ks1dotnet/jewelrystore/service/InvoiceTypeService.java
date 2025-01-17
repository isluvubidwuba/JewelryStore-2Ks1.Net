package com.ks1dotnet.jewelrystore.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.repository.IInvoiceTypeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IInvoiceTypeService;

@Service
public class InvoiceTypeService implements IInvoiceTypeService {

        @Autowired
        private IInvoiceTypeRepository invoiceTypeRepository;

        @Override
        public ResponseData getAllInvoiceTypes() {
                List<InvoiceTypeDTO> invoiceTypeDTOs = invoiceTypeRepository.findAll().stream()
                                .map(InvoiceType::getDTO).collect(Collectors.toList());
                return new ResponseData(HttpStatus.OK, "Fetched all invoice types",
                                invoiceTypeDTOs);
        }

        @Override
        public ResponseData getInvoiceTypeById(int id) {
                InvoiceType invoiceType = invoiceTypeRepository.findById(id)
                                .orElseThrow(() -> new ApplicationException(
                                                "Invoice type not found with id: " + id,
                                                HttpStatus.NOT_FOUND));
                return new ResponseData(HttpStatus.OK, "Fetched invoice type by id",
                                invoiceType.getDTO());
        }

        @Override
        public ResponseData createInvoiceType(String name) {
                InvoiceType invoiceType = new InvoiceType();
                invoiceType.setName(name);
                InvoiceType savedInvoiceType = invoiceTypeRepository.save(invoiceType);
                return new ResponseData(HttpStatus.CREATED, "Created invoice type",
                                savedInvoiceType.getDTO());
        }

        @Override
        public ResponseData updateInvoiceType(int id, float rate) {
                InvoiceType invoiceType = invoiceTypeRepository.findById(id)
                                .orElseThrow(() -> new ApplicationException(
                                                "Invoice type not found with id: " + id,
                                                HttpStatus.NOT_FOUND));
                invoiceType.setRate(rate);
                InvoiceType updatedInvoiceType = invoiceTypeRepository.save(invoiceType);
                return new ResponseData(HttpStatus.OK, "Updated invoice type successfully !",
                                updatedInvoiceType.getDTO());
        }

        @Override
        public InvoiceType findById(Integer id) {
                return invoiceTypeRepository.findById(id)
                                .orElseThrow(() -> new ApplicationException(
                                                "InvoiceType not found with id: " + id,
                                                HttpStatus.NOT_FOUND));
        }
}
