package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.ProductCategoryDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "product_category")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "productCategory")
    Set<ForProductType> listForProductType;

    @OneToMany(mappedBy = "productCategory")
    Set<Product> list_product;

    @OneToMany(mappedBy = "productCategory")
    Set<WareHouse> listWareHouse;

    public ProductCategoryDTO getDTO() {
        return new ProductCategoryDTO(this.id, this.name);
    }

    public ProductCategory(ProductCategoryDTO t) {
        this.id = t.getId();
        this.name = t.getName();
    }
}