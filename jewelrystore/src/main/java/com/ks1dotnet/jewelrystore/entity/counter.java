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
@Table(name = "counter")
public class counter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToMany(mappedBy = "counter")
    Set<assign_counters_for_staff> ListAssignCountersForStaff;

    @OneToMany(mappedBy = "counter")
    Set<product> list_product;

    @OneToMany(mappedBy = "counter")
    Set<order_invoice_detail> list_order_invoice_detail;
}
