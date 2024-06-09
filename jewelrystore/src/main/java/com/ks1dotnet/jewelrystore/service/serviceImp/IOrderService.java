package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IOrderService {

    public ResponseData insertOrder(OrderInvoiceDTO orderInvoiceRequest);

    public ResponseData getOrderInvoiceDetail(String barcode, String IdExchangeRate);
}
