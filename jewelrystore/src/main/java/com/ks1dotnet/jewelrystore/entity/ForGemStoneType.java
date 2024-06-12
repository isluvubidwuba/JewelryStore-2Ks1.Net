package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.ForGemStoneTypeDTO;

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
@Table(name = "for_gemstone_type")
public class ForGemStoneType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_gemstone_type")
    private GemStoneType gemstoneType;

    public ForGemStoneTypeDTO getDTO() {
        return new ForGemStoneTypeDTO(this.id, this.promotion.getDTO(), this.gemstoneType.getDTO(), this.status);
    }

    public ForGemStoneType(ForGemStoneTypeDTO g) {
        this.id = g.getId();
        this.promotion = new Promotion(g.getPromotionDTO());
        this.gemstoneType = new GemStoneType(g.getGemStoneTypeDTO());
        this.status = g.isStatus();
    }

    public ForGemStoneType(Promotion promotion2, GemStoneType gemstoneType2, boolean b) {
        this.promotion = promotion2;
        this.gemstoneType = gemstoneType2;
        this.status = b;
    }
}
