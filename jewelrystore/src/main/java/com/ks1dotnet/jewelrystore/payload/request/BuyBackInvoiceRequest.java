package com.ks1dotnet.jewelrystore.payload.request;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BuyBackInvoiceRequest {
    private InvoiceRequest request;
    private Map<Integer, Integer> idDetailQuantityMap;
}
