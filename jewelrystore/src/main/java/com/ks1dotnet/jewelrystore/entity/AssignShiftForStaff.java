package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.AssignShiftForStaffDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "assign_shift_for_staff")
public class AssignShiftForStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "check_in")
    private LocalDate checkIn;

    @Column(name = "check_out")
    private LocalDate checkOut;

    @Column(name = "is_late")
    private boolean isLate;
    
    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @OneToMany
    @JoinColumn(name = "assignShiftForStaff")
    Set<AssignCountersForStaff> listAssignCountersForStaff;

    public AssignShiftForStaffDTO getDTO(){
        return new AssignShiftForStaffDTO(this.id, this.date, this.checkIn, this.checkOut, this.isLate, this.note, this.employee.getDTO());
    }

    public AssignShiftForStaff(AssignShiftForStaffDTO e){
        this.id = e.getId();
        this.date = e.getDate();
        this.checkIn = e.getCheckIn();
        this.checkOut = e.getCheckOut();
        this.isLate = e.isLate();
        this.note = e.getNote();
        this.employee = new Employee(e.getEmployeeDTO());
    }


}
