package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.GemStoneOfProductDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gemstone_of_product")
public class GemStoneOfProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "color")
    private String color;
    @Column(name = "clarity")
    private String clarity;
    @Column(name = "carat")
    private float carat;
    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_type")
    private GemStoneType gemstoneType;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_category")
    private GemStoneCategory gemstoneCategory;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public GemStoneOfProductDTO getDTO() {
        return new GemStoneOfProductDTO(
                this.id,
                this.color,
                this.clarity,
                this.carat,
                this.price,
                this.gemstoneType.getDTO(),
                this.gemstoneCategory.getDTO(),
                this.product.getDTO());
    }

    public GemStoneOfProduct(GemStoneOfProductDTO t) {
        this.id = t.getId();
        this.color = t.getColor();
        this.clarity = t.getClarity();
        this.carat = t.getCarat();
        this.price = t.getPrice();
        this.gemstoneType = new GemStoneType(t.getGemstoneType());
        this.gemstoneCategory = new GemStoneCategory(t.getGemstoneCategory());
        this.product = new Product(t.getProduct());
    }
}
