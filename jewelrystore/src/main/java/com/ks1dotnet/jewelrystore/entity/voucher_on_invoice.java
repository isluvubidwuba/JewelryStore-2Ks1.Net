package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "voucher_on_invoice")
public class voucher_on_invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_invoice")
    private order_invoice order_invoice;
}
