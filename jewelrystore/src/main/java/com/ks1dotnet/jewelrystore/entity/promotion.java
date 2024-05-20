package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "promotion")
public class promotion {
    @Id
    private String id;
    private int name;
    private double value;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_voucher_type")
    private voucher_type voucher_type;

    @OneToMany(mappedBy = "promotion")
    Set<for_product> list_for_product;

    @OneToMany(mappedBy = "promotion")
    Set<voucher_on_invoice> list_voucher_on_invoice;

    @OneToMany(mappedBy = "promotion")
    Set<voucher_on_invoice_detail> list_voucher_on_invoice_detail;
}
