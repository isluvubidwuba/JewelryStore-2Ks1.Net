package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.ProductDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "fee")
    private double fee;
    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_material_of_product")
    private MaterialOfProduct materialOfProduct;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;

    @ManyToOne
    @JoinColumn(name = "id_counter")
    private Counter counter;

    @OneToMany(mappedBy = "product")
    Set<GemStoneOfProduct> listGemStoneOfProduct;

    @OneToMany(mappedBy = "product")
    Set<ForProduct> listForProduct;

    @OneToOne(mappedBy = "product")
    private WareHouse wareHouse;

    @OneToMany(mappedBy = "product")
    Set<OrderInvoiceDetail> listOrderInvoiceDetail;

    public ProductDTO getDTO() {
        return new ProductDTO(this.id, this.name, this.fee, this.status, this.materialOfProduct.getDTO(),
                this.productCategory.getDTO(), this.counter.getDTO());
    }

    public Product(ProductDTO t) {
        this.id = t.getId();
        this.name = t.getName();
        this.fee = t.getFee();
        this.status = t.isStatus();
        this.materialOfProduct = new MaterialOfProduct(t.getMaterialOfProductDTO());
        this.productCategory = new ProductCategory(t.getProductCategoryDTO());
        this.counter = new Counter(t.getCounterDTO());
    }
}