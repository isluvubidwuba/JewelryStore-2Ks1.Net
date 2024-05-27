package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.payload.responseData;

public interface IInvoiceTypeService {

    public responseData createInvoiceType(String name);

    public List<InvoiceTypeDTO> getFullInvoice();

    public responseData addInvoiceType(String invoiceType);

    public responseData updateInvoice(int idInvoiceType, String invoiceType);

}
