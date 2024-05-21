package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private double value;
    @Column(name = "status")
    private boolean status;
    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "id_voucher_type")
    private VoucherType voucherType;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.EAGER)
    Set<ForProduct> listForProduct;

    @OneToMany(mappedBy = "promotion")
    Set<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "promotion")
    Set<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;
}
