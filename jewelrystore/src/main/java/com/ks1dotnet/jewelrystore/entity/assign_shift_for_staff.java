package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
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
@Table(name = "assign_shift_for_staff")
public class assign_shift_for_staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date date;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private employee employee;

    @OneToMany(mappedBy = "assign_shift_for_staff")
    Set<timekeeping> list_timekeeping;

    @OneToMany(mappedBy = "assign_shift_for_staff")
    Set<assign_counters_for_staff> list_assign_counters_for_staff;
}
