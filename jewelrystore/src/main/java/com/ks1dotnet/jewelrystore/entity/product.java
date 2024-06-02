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
    private int id;
    @Column(name = "product_code")
    private String productCode;
    @Column(name = "barcode", unique = true)
    private String barCode;
    @Column(name = "name")
    private String name;
    @Column(name = "fee")
    private double fee;
    @Column(name = "status")
    private boolean status;
    @Column(name = "weight")
    private Float weight;
    @Column(name = "img")
    private String imgPath;
    @ManyToOne
    @JoinColumn(name = "id_material")
    private Material material;

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
        return new ProductDTO(this.id, this.getProductCode(), this.getBarCode(), this.name, this.fee, this.status,
                this.weight, this.material.getDTO(),
                this.productCategory.getDTO(), this.counter.getDTO(), this.imgPath);
    }

    public Product(ProductDTO t) {
        if (t.getId() != 0)
            this.id = t.getId();
        if (t.getProductCode() != null)
            this.productCode = t.getProductCode();
        if (t.getBarCode() != null)
            this.barCode = t.getBarCode();
        if (t.getName() != null)
            this.name = t.getName();
        if (t.getFee() != null)
            this.fee = t.getFee();
        this.status = t.isStatus();
        if (t.getMaterialDTO() != null)
            this.material = new Material(t.getMaterialDTO());
        if (t.getProductCategoryDTO() != null)
            this.productCategory = new ProductCategory(t.getProductCategoryDTO());
        if (t.getCounterDTO() != null)
            this.counter = new Counter(t.getCounterDTO());
        if (t.getImgPath() != null)
            this.imgPath = t.getImgPath();
    }
}
