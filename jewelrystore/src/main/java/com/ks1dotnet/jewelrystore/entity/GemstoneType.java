package com.ks1dotnet.jewelrystore.entity;

import java.util.List;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.GemStoneTypeDTO;

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
@Table(name = "gemstone_type")
public class GemStoneType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "gemstoneType")
    Set<GemStoneOfProduct> listGemstoneOfProduct;

    @OneToMany(mappedBy = "gemstoneType")
    List<ForGemStoneType> listForGemStoneTypes;

    public GemStoneTypeDTO getDTO() {
        return new GemStoneTypeDTO(this.id, this.name);
    }

    public GemStoneType(GemStoneTypeDTO t) {
        this.id = t.getId();
        this.name = t.getName();
    }
}
