package com.ks1dotnet.jewelrystore.entity;

import java.sql.Date;
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
@Table(name = "order_invoice")
public class order_invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date date;
    private double total_price_raw;
    private double total_price;
    private double discount_price;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private user_info user_info;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private employee employee;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private invoice_type invoice_type;

    @OneToMany(mappedBy = "order_invoice")
    Set<voucher_on_invoice> list_voucher_on_invoice;

    @OneToMany(mappedBy = "order_invoice")
    Set<order_invoice_detail> list_order_invoice_detail;
}
