package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.CascadeType;
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

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, quantity, total_import, total_sold);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        WareHouse wareHouse = (WareHouse) o;
        return id == wareHouse.id &&
                quantity == wareHouse.quantity &&
                total_import == wareHouse.total_import &&
                total_sold == wareHouse.total_sold;
    }
}