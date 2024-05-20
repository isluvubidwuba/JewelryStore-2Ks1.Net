package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "gemstone_of_product")
public class GemstoneOfProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "color")
    private String color;
    @Column(name = "clarity")
    private String clarity;
    @Column(name = "carat")
    private float carat;
    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_type")
    private GemstoneType gemstoneType;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_category")
    private GemstoneCategory gemstoneCategory;

    @OneToMany(mappedBy = "gemstoneOfProduct")
    Set<Product> listProduct;
}
