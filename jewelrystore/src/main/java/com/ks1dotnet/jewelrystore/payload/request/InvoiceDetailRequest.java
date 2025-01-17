package com.ks1dotnet.jewelrystore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDetailRequest {
    private String barcode;
    private Integer invoiceTypeId;
    private Integer quantity;
}