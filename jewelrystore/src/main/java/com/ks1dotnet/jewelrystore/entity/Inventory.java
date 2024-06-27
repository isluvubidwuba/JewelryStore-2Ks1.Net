package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.InventoryDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "inventory")
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToOne
    @JoinColumn(name = "id_product", referencedColumnName = "id")
    private Product product;
    private int quantity;
    private int total_sold;
    private int total_import;

    public InventoryDTO getDTO() {
        return new InventoryDTO(this.quantity);
    }
  
}
