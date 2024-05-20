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
public class policy_for_invoice {
    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private invoice_type invoice_type;

    @ManyToOne
    @JoinColumn(name = "id_rate_policy")
    private exchange_rate_policy exchange_rate_policy;
}
