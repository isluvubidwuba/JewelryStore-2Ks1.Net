package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;

public interface IInvoiceService {
    public InvoiceDetailDTO createInvoiceDetail(String barcode, Integer invoiceType, Integer quantity);
}
