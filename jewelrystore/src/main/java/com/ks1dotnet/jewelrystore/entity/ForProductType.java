package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "for_product_type")
public class ForProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    @ManyToOne
    @JoinColumn(name = "id_voucher_type")
    private VoucherType voucherType;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;
}
