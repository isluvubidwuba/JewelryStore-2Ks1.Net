package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

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
    private Integer id;
    @Column(name = "type")
    private Integer type;
    @Column(name = "point_condition")
    private Integer pointCondition;

    @OneToMany(mappedBy = "customerType")
    Set<ForCustomer> listForCustomer;

    @OneToMany(mappedBy = "customerType")
    Set<EarnPoints> listEarnPoints;
}
