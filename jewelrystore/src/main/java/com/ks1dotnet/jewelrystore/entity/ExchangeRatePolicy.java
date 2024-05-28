package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exchange_rate_policy")
public class ExchangeRatePolicy {
    @Id
    private String id;
    @Column(name = "description_policy")
    private String description_policy;
    @Column(name = "rate")
    private Float rate;
    @Column(name = "status")
    private boolean status;
    @Column(name = "last_modified")
    private LocalDate lastModified;

    @OneToMany(mappedBy = "exchangeRatePolicy")
    Set<PolicyForInvoice> listPolicyForInvoice;

    public ExchangeRatePolicyDTO getDTO() {
        return new ExchangeRatePolicyDTO(this.id, this.description_policy, this.rate, this.status, this.lastModified);
    }

    public void setLastModified() {
        this.lastModified = LocalDate.now();
    }

    public ExchangeRatePolicy(ExchangeRatePolicyDTO e) {
        this.id = e.getId();
        this.description_policy = e.getDescription_policy();
        this.rate = e.getRate();
        this.status = e.isStatus();
        this.lastModified = e.getLastModified();
    }
}