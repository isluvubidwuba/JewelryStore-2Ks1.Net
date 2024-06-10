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
    private String img;

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
                this.weight, this.img, this.material.getDTO(),
                this.productCategory.getDTO(), this.counter.getDTO());
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
        if (t.getImg() != null)
            this.img = t.getImg();

        this.status = t.isStatus();
        if (t.getMaterialDTO() != null)
            this.material = new Material(t.getMaterialDTO());
        if (t.getProductCategoryDTO() != null)
            this.productCategory = new ProductCategory(t.getProductCategoryDTO());
        if (t.getCounterDTO() != null)
            this.counter = new Counter(t.getCounterDTO());
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, productCode, barCode, name, fee, status, weight, img);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Product product = (Product) o;
        return id == product.id &&
                Double.compare(product.fee, fee) == 0 &&
                status == product.status &&
                java.util.Objects.equals(productCode, product.productCode) &&
                java.util.Objects.equals(barCode, product.barCode) &&
                java.util.Objects.equals(name, product.name) &&
                java.util.Objects.equals(weight, product.weight) &&
                java.util.Objects.equals(img, product.img);
    }
}