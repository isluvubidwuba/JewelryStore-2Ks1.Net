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
@Table(name = "invoice_type")
public class InvoiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToMany(mappedBy = "invoiceType")
    Set<PolicyForInvoice> listPolicyForInvoice;

    @OneToMany(mappedBy = "invoiceType")
    Set<OrderInvoice> listOrderInvoice;

}
