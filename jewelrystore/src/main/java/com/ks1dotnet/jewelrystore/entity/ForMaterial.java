package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.ForMaterialDTO;

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
@Table(name = "for_material")
public class ForMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_material")
    private Material material;

    public ForMaterialDTO getDTO() {
        return new ForMaterialDTO(this.id, this.promotion.getDTO(), this.material.getDTO(), this.status);
    }

    public ForMaterial(ForMaterialDTO m) {
        this.id = m.getId();
        this.promotion = new Promotion(m.getPromotionDTO());
        this.material = new Material(m.getMaterialDTO());
        this.status = m.isStatus();
    }

    public ForMaterial(Promotion promotion2, Material material2, boolean b) {
        this.promotion = promotion2;
        this.material = material2;
        this.status = b;
    }
}
