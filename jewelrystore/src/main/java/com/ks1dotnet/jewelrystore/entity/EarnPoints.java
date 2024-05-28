package com.ks1dotnet.jewelrystore.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "earn_points")
public class EarnPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "point")
    private Integer point;

    @ManyToOne
    @JoinColumn(name = "id_customer_info")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "id_customer_type")
    private CustomerType customerType;
}
