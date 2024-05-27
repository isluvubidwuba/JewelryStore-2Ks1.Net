package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.MaterialOfProductDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "material_of_product")
public class MaterialOfProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "weight")
    private Float weight;

    @ManyToOne
    @JoinColumn(name = "id_material")
    private Material material;

    @OneToMany(mappedBy = "materialOfProduct")
    Set<Product> listProduct;

    public MaterialOfProductDTO getDTO() {
        return new MaterialOfProductDTO(this.id, this.weight, this.material.getDTO());
    }

    public MaterialOfProduct(MaterialOfProductDTO t) {
        this.id = t.getId();
        this.weight = t.getWeight();
        this.material = new Material(t.getMaterialDTO());
    }
}