package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "voucher_type")
public class voucher_type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int type;

    @OneToMany(mappedBy = "voucher_type")
    Set<promotion> list_promotions;

    @OneToMany(mappedBy = "voucher_type")
    Set<for_customer> list_for_customer;

    @OneToMany(mappedBy = "voucher_type")
    Set<for_product_type> list_for_product_type;

}
