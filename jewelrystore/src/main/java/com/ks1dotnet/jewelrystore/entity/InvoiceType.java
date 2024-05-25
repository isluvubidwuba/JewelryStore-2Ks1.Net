package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "invoice_type")
public class InvoiceType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "invoiceType")
    Set<PolicyForInvoice> listPolicyForInvoice;

    @OneToMany(mappedBy = "invoiceType")
    Set<OrderInvoice> listOrderInvoice;

    public InvoiceTypeDTO getDTO() {
        return new InvoiceTypeDTO(this.id, this.name);
    }

    public InvoiceType(InvoiceTypeDTO i) {
        this.id = i.getId();
        this.name = i.getName();
    }

}
