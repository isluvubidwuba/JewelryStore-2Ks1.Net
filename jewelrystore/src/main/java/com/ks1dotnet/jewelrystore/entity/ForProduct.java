package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.ForProductDTO;

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
@Table(name = "for_product")
public class ForProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    public ForProduct(Promotion promotion, Product product, boolean status) {
        this.promotion = promotion;
        this.product = product;
        this.status = status;
    }

    public ForProductDTO getDTO() {
        return new ForProductDTO(id, this.promotion.getDTO(), this.product.getDTO(), this.status);
    }
}
