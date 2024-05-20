package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

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
public class gemstone_of_product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String color;
    private String clarity;
    private float carat;
    private double price;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_type")
    private gemstone_type gemstone_type;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_category")
    private gemstone_category gemstone_category;

    @OneToMany(mappedBy = "gemstone_of_product")
    Set<product> list_product;
}
