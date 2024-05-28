package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IInvoiceTypeService {

    public ResponseData createInvoiceType(String name);

    public List<InvoiceTypeDTO> getFullInvoice();

    public ResponseData addInvoiceType(String invoiceType);

    public ResponseData updateInvoice(int idInvoiceType, String invoiceType);

}
