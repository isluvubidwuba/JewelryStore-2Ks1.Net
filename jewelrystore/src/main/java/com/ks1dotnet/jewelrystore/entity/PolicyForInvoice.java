package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "policy_for_invoice")
public class PolicyForInvoice {
    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @ManyToOne
    @JoinColumn(name = "id_rate_policy")
    private ExchangeRatePolicy exchangeRatePolicy;
}
