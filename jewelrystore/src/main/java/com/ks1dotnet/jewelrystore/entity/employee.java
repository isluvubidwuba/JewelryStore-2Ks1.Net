package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "employee")
public class employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String first_name;
    private String last_name;
    @Column(name = "pin_code")
    private String pinCode;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private role role;

    @OneToMany(mappedBy = "employee")
    Set<assign_shift_for_staff> ListAssignShiftForStaff;

    @OneToMany(mappedBy = "employee")
    Set<order_invoice> list_order_invoice;
}
