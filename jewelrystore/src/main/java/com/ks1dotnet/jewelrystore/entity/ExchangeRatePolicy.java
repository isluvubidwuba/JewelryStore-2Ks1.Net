package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDate;

import com.ks1dotnet.jewelrystore.dto.ExchangeRatePolicyDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    public ExchangeRatePolicyDTO getDTO() {
        return new ExchangeRatePolicyDTO(this.id, this.description_policy, this.rate, this.status, this.lastModified,
                this.invoiceType.getDTO());
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
        this.invoiceType = new InvoiceType(e.getInvoiceTypeDTO());
    }
}