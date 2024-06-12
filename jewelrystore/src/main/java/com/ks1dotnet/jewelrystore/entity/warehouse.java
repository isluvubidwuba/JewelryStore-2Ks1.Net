package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class WareHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "barcode_import")
    private String barcodeImport;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_import")
    private int totalImport;

    @Column(name = "total_sold")
    private int totalSold;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_order_invoice")
    private OrderInvoice orderInvoice;

}