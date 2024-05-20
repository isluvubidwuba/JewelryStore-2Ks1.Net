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
@Table(name = "material_of_product")
public class MaterialOfProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private float weight;

    @ManyToOne
    @JoinColumn(name = "id_material")
    private Material material;

    @OneToMany(mappedBy = "materialOfProduct")
    Set<Product> listProduct;
}
