package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IOrderService {

    // ResponseData insertOrder(OrderRequest orderRequest);

    public ResponseData getOrderInvoiceDetail(String barcode, String IdExchangeRate);
}
