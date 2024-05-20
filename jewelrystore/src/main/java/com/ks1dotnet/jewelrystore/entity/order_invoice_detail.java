package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

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
public class order_invoice_detail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private double price;
    private double price_material_at_time;
    private int quantity;
    private double total_price;

    @ManyToOne
    @JoinColumn(name = "id_order_invoice")
    private order_invoice order_invoice;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private product product;

    @ManyToOne
    @JoinColumn(name = "id_counter_at_time")
    private counter counter;

    @OneToMany(mappedBy = "order_invoice_detail")
    Set<voucher_on_invoice_detail> list_voucher_on_invoice_detail;
}
