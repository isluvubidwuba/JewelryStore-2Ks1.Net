package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;
import com.ks1dotnet.jewelrystore.dto.PolicyForInvoiceDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "policy_for_invoice")
public class PolicyForInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @ManyToOne
    @JoinColumn(name = "id_rate_policy")
    private ExchangeRatePolicy exchangeRatePolicy;

    public PolicyForInvoiceDTO getDTO() {
        return new PolicyForInvoiceDTO(this.id, this.invoiceType.getDTO(), this.exchangeRatePolicy.getDTO());
    }

    public PolicyForInvoice(PolicyForInvoiceDTO p) {
        this.id = p.getId();
        this.invoiceType = new InvoiceType(p.getInvoiceTypeDTO());
        this.exchangeRatePolicy = new ExchangeRatePolicy(p.getExchangeRatePolicyDTO());
    }
}
