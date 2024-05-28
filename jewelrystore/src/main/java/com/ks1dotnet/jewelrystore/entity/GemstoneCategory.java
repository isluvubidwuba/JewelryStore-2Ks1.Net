package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.GemStoneCategoryDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "gemstone_category")
public class GemStoneCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "gemstoneCategory")
    Set<GemStoneOfProduct> listGemstoneOfProduct;

    public GemStoneCategoryDTO getDTO() {
        return new GemStoneCategoryDTO(this.id, this.name);
    }

    public GemStoneCategory(GemStoneCategoryDTO dto) {
        this.id = dto.getId();
        this.name = dto.getName();
    }
}
