package com.ks1dotnet.jewelrystore.entity;

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
import lombok.Data;

@Data
@Entity
@Table(name = "order_invoice_detail")
public class OrderInvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "price")
    private double price;
    @Column(name = "price_material_at_time")
    private double priceMaterialAtTime;
    @Column(name = "quantity")
    private int quantity;
    @Column(name = "total_price")
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "id_order_invoice")
    private OrderInvoice orderInvoice;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_counter_at_time")
    private Counter counter;

    @OneToMany(mappedBy = "orderInvoiceDetail")
    Set<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;
}
