package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherOnInvoiceDTO {
    private Integer id;
    private PromotionDTO promotionDTO;
    private InvoiceDTO invoice;

}
