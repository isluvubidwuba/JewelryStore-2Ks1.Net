package com.ks1dotnet.jewelrystore.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.InvoiceTypeDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "rate")
    private float rate;

    @OneToMany(mappedBy = "invoiceType")
    Set<Invoice> listOrderInvoice;

    public InvoiceTypeDTO getDTO() {
        return new InvoiceTypeDTO(this.id, this.name, this.rate);
    }

    @OneToMany(mappedBy = "invoiceType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Promotion> listForCustomer = new ArrayList<>();

    public InvoiceType(InvoiceTypeDTO i) {
        this.id = i.getId();
        this.name = i.getName();
        this.rate = i.getRate();
    }

}