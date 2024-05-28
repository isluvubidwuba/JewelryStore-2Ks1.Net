package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "warehouse")
public class WareHouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int quantity;
    private int total_import;
    private int total_sold;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;
}
