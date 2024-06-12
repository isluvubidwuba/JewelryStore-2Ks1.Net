package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.CustomerTypeDTO;

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
@Table(name = "customer_type")
public class CustomerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "type")
    private String type;
    
    @Column(name = "point_condition")
    private Integer pointCondition;

    @OneToMany(mappedBy = "customerType")
    Set<ForCustomer> listForCustomer;

    @OneToMany(mappedBy = "customerType")
    Set<EarnPoints> listEarnPoints;

    public CustomerTypeDTO getDTO() {
        return new CustomerTypeDTO(this.id, this.type, this.pointCondition);
    }

    public CustomerType(CustomerTypeDTO t) {
        this.id = t.getId();
        this.type = t.getType();
        this.pointCondition = t.getPointCondition();
    }

}
