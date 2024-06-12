package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.List;

import com.ks1dotnet.jewelrystore.dto.MaterialDTO;

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
@Table(name = "material")
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "purity")
    private String purity;

    @Column(name = "price_at_time")
    private Double priceAtTime;

    @Column(name = "last_modified")
    private Date lastModified;

    @OneToMany(mappedBy = "material")
    List<Product> listProduct;
    @OneToMany(mappedBy = "material")
    List<ForMaterial> listForMaterials;

    public MaterialDTO getDTO() {
        return new MaterialDTO(this.id, this.name, this.purity, this.priceAtTime, this.lastModified);
    }

    public Material(MaterialDTO t) {
        if (t.getId() != 0)
            this.id = t.getId();
        if (t.getName() != null)
            this.name = t.getName();
        if (t.getPurity() != null)
            this.purity = t.getPurity();
        if (t.getPriceAtTime() != 0)
            this.priceAtTime = t.getPriceAtTime();
        if (t.getLastModified() != null)
            this.lastModified = t.getLastModified();
    }
}
