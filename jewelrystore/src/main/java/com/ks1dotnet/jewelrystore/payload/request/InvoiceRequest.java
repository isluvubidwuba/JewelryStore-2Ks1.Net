package com.ks1dotnet.jewelrystore.payload.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
    private Map<String, String> barcodeQuantityMap;
    private Integer invoiceTypeId;
    private Integer userId;
    private String employeeId;
    private String payment;
    private String note;
}
