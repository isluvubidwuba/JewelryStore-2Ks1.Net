package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "earn_points")
public class earn_points {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int point;

    @ManyToOne
    @JoinColumn(name = "id_customer_info")
    private user_info user_info;

    @ManyToOne
    @JoinColumn(name = "id_customer_type")
    private customer_type customer_type;
}
