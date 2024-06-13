package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.HashMap;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;

public interface IInvoiceService {
    public InvoiceDetailDTO createInvoiceDetail(String barcode, Integer invoiceType, Integer quantity);

    public int createInvoiceFromDetails(HashMap<String, Integer> barcodeQuantity, Integer invoiceTypeId, Integer userId,
            String employeeId);
}
