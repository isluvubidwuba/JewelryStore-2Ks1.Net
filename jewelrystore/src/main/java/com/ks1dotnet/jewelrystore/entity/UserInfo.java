package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String full_name;
    private String phone_number;
    private String email;
    private String address;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

    @OneToMany(mappedBy = "userInfo")
    Set<EarnPoints> listEarnPoints;

    @OneToMany(mappedBy = "userInfo")
    Set<OrderInvoice> listOrderInvoice;
}
