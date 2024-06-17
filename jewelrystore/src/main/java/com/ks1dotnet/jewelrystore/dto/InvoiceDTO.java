package com.ks1dotnet.jewelrystore.dto;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDTO {
    private Integer id;
    private UserInfoDTO userInfoDTO;
    private EmployeeDTO employeeDTO;
    private InvoiceTypeDTO invoiceTypeDTO;
    private Date date;
    private boolean status;
    private double totalPriceRaw;
    private double totalPrice;
    private double discountPrice;
    private String payment;
    private String note;
    private List<PromotionDTO> listPromotionOnInvoice;
    private List<InvoiceDetailDTO> listOrderInvoiceDetail;
}
