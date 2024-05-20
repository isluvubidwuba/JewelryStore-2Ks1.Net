package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product_category")
public class product_category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToMany(mappedBy = "product_category")
    Set<for_product_type> list_for_product_type;

    @OneToMany(mappedBy = "product_category")
    Set<product> list_product;

    @OneToMany(mappedBy = "product_category")
    Set<warehouse> list_warehouse;

}
