package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    private String id;
    private String name;
    private double fee;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_material_of_product")
    private MaterialOfProduct materialOfProduct;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_of_product")
    private GemstoneOfProduct gemstoneOfProduct;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "id_counter")
    private Counter counter;

    @OneToMany(mappedBy = "product")
    Set<ForProduct> listForProduct;

    @OneToMany(mappedBy = "product")
    Set<WareHouse> listWareHouse;

    @OneToMany(mappedBy = "product")
    Set<OrderInvoiceDetail> listOrderInvoiceDetail;
}
