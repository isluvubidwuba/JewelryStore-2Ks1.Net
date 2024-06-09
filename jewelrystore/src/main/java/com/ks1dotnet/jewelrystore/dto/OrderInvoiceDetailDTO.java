package com.ks1dotnet.jewelrystore.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInvoiceDetailDTO {
    private ProductDTO productDTO;
    private double price;
    private int quantity;
    private double totalPrice;
    private List<PromotionDTO> listPromotion;
}
