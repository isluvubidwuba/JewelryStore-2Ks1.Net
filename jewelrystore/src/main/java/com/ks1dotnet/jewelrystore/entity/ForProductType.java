package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.ForProductTypeDTO;

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
@Table(name = "for_product_type")
public class ForProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Đổi từ Id thành id

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_product_category")
    private ProductCategory productCategory;

    private boolean status;

    public ForProductType(Promotion promotion, ProductCategory productCategory, boolean status) {
        this.promotion = promotion;
        this.productCategory = productCategory;
        this.status = status;
    }

    public ForProductTypeDTO getDTO() {
        return new ForProductTypeDTO(this.id, this.promotion.getDTO(), this.productCategory.getDTO(), this.status); // Đổi
                                                                                                                    // từ
                                                                                                                    // this.Id
        // thành this.id
    }
}
