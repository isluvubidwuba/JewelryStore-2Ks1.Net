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
public class Promotion {
    @Id
    private String id;
    private int name;
    private double value;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_voucher_type")
    private VoucherType voucherType;

    @OneToMany(mappedBy = "promotion")
    Set<ForProduct> listForProduct;

    @OneToMany(mappedBy = "promotion")
    Set<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "promotion")
    Set<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;
}
