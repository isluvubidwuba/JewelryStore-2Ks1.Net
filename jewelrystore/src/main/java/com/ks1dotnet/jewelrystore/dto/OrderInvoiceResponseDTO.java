package com.ks1dotnet.jewelrystore.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInvoiceResponseDTO {
    private Integer id;
    private UserInfoDTO userInfo;
    private EmployeeDTO employee;
    private InvoiceTypeDTO invoiceType;
    private double totalPriceRaw;
    private double totalPrice;
    private double discountPrice;
    private Date date;
    private List<OrderInvoiceDetailDTO> listOrderInvoiceDetail;
    private List<PromotionDTO> promotions;
}
