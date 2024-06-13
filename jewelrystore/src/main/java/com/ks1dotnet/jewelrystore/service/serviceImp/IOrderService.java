package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IOrderService {

    public ResponseData insertOrder(InvoiceDTO orderInvoiceRequest);

    public ResponseData getOrderInvoiceDetail(String barcode, String IdExchangeRate);
}
