package com.ks1dotnet.jewelrystore.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInvoiceDTO {
    private Integer id;
    private Integer idUser;
    private String idEmployee;
    private Integer idInvoiceType;
    private double totalPriceRaw;
    private double totalPrice;
    private double discountPrice;
    private List<OrderInvoiceDetailDTO> listOrderInvoiceDetail;
}
