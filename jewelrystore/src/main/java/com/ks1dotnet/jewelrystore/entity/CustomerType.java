package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "customer_type")
public class CustomerType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type")
    private int type;
    @Column(name = "point_condition")
    private int pointCondition;

    @OneToMany(mappedBy = "customerType")
    Set<ForCustomer> listForCustomer;

    @OneToMany(mappedBy = "customerType")
    Set<EarnPoints> listEarnPoints;
}
