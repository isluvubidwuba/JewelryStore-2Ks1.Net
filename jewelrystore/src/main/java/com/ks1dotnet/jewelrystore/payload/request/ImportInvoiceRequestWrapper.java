package com.ks1dotnet.jewelrystore.payload.request;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportInvoiceRequestWrapper {
    private InvoiceRequest request;
    private Map<String, Double> barcodePriceMap;
}
