package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exchange_rate_policy")
public class ExchangeRatePolicy {
    @Id
    private String id;
    @Column (name = "description_policy")
    private String description_policy;
    @Column(name = "rate")
    private float rate;
    @Column(name = "status")
    private boolean status;
    @Column(name = "last_modified")
    private Date lastModified;

    @OneToMany(mappedBy = "exchangeRatePolicy")
    Set<PolicyForInvoice> listPolicyForInvoice;
}
