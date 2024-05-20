package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

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
public class customer_type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int type;
    private int point_condition;

    @OneToMany(mappedBy = "customer_type")
    Set<for_customer> list_for_customer;

    @OneToMany(mappedBy = "customer_type")
    Set<earn_points> list_earn_points;
}
