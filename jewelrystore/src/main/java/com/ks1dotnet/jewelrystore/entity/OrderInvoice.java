package com.ks1dotnet.jewelrystore.entity;

import java.sql.Date;
import java.util.Set;

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
    @Column(name = "total_price_raw")
    private double totalPriceRaw;
    @Column(name = "total_price")
    private double totalPrice;
    @Column(name = "discount_price")
    private double discountPrice;

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
    Set<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "orderInvoice")
    Set<OrderInvoiceDetail> listOrderInvoiceDetail;
}
