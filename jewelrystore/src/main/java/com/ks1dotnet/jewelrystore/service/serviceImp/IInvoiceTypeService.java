package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IInvoiceTypeService {
    ResponseData getAllInvoiceTypes();

    ResponseData getInvoiceTypeById(int id);

    ResponseData createInvoiceType(String name);

    ResponseData updateInvoiceType(int id, String name);

}
