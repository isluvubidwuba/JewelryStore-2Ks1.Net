package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "timekeeping")
public class Timekeeping {
    @Id
    private int id;
    private Date check_in;
    private Date check_out;
    private boolean is_late;
    private String note;

    @ManyToOne
    @JoinColumn(name = "id_assign_shift")
    private AssignShiftForStaff assignShiftForStaff;

}
