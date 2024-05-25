package com.ks1dotnet.jewelrystore.dto;

import com.ks1dotnet.jewelrystore.entity.InvoiceType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PolicyForInvoiceDTO {
    private int id;
    private InvoiceTypeDTO invoiceTypeDTO;
    private ExchangeRatePolicyDTO exchangeRatePolicyDTO;

}
