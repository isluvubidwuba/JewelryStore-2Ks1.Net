package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;

import jakarta.persistence.Column;
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
    private Integer id;
    @Column(name = "check_in")
    private Date checkIn;
    @Column(name = "check_out")
    private Date checkOut;
    @Column(name = "is_late")
    private boolean isLate;
    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "id_assign_shift")
    private AssignShiftForStaff assignShiftForStaff;

}
