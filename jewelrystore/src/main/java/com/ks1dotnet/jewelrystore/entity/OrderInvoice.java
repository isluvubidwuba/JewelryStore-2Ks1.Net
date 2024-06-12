package com.ks1dotnet.jewelrystore.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.OrderInvoiceResponseDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_invoice")
public class OrderInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "date")
    private Date date;
    private double total_price_raw;
    private double total_price;
    private double discount_price;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @OneToMany(mappedBy = "orderInvoice")
    List<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "orderInvoice")
    List<OrderInvoiceDetail> listOrderInvoiceDetail;

    public OrderInvoiceResponseDTO gResponseDTO() {
        List<OrderInvoiceDetailDTO> listOrderInvoiceDetail = new ArrayList<>();
        for (OrderInvoiceDetail orderInvoiceDetail : this.listOrderInvoiceDetail) {
            listOrderInvoiceDetail.add(orderInvoiceDetail.getDTO());
        }
        List<PromotionDTO> promotions = new ArrayList<>();
        for (VoucherOnInvoice voucherOnInvoice : this.listVoucherOnInvoice) {
            promotions.add(voucherOnInvoice.getPromotion().getDTO());
        }
        return new OrderInvoiceResponseDTO(this.id, this.userInfo.getDTO(), this.employee.getDTO(),
                this.invoiceType.getDTO(),
                total_price_raw, total_price, discount_price, date, listOrderInvoiceDetail, promotions);

    }

    // private Integer id;
    // private UserInfoDTO userInfo;
    // private EmployeeDTO employee;
    // private InvoiceTypeDTO invoiceType;
    // private double totalPriceRaw;
    // private double totalPrice;
    // private double discountPrice;
    // private Date date;
    // private List<OrderInvoiceDetailDTO> listOrderInvoiceDetail;
    // private List<PromotionDTO> promotions;

}
