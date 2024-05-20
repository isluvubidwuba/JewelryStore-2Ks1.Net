package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "purity")
    private String purity;
    @Column(name = "price_at_time")
    private Double priceAtTime;
    @Column(name = "last_modified")
    private Date lastModified;

    @OneToMany(mappedBy = "material")
    Set<MaterialOfProduct> listMaterialOfProduct;
}
