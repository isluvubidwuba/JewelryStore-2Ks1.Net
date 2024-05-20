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
public class product {
    @Id
    private String id;
    private String name;
    private double fee;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_material_of_product")
    private material_of_product material_of_product;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_of_product")
    private gemstone_of_product gemstone_of_product;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private product_category product_category;

    @ManyToOne
    @JoinColumn(name = "id_counter")
    private counter counter;

    @OneToMany(mappedBy = "product")
    Set<for_product> list_for_product;

    @OneToMany(mappedBy = "product")
    Set<warehouse> list_warehouse;

    @OneToMany(mappedBy = "product")
    Set<order_invoice_detail> list_order_invoice_detail;
}
