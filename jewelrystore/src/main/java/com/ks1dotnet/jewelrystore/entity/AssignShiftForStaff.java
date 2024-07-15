package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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

    @Column(name = "check_in", nullable = true)
    private LocalDateTime checkIn;

    @Column(name = "check_out", nullable = true)
    private LocalDateTime checkOut;

    @Column(name = "is_late", nullable = true)
    private Boolean isLate; // Sử dụng Boolean thay vì boolean để cho phép giá trị null

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @OneToMany(mappedBy = "assignShiftForStaff")
    List<AssignCountersForStaff> listAssignCountersForStaff;

    public AssignShiftForStaffDTO getDTO() {
        return new AssignShiftForStaffDTO(this.id, this.date, this.checkIn, this.checkOut, this.isLate,
                this.employee.getDTO());
    }

    public AssignShiftForStaff(AssignShiftForStaffDTO e) {
        this.id = e.getId();
        this.date = e.getDate();
        this.checkIn = e.getCheckIn();
        this.checkOut = e.getCheckOut();
        this.isLate = e.getIsLate();
        this.employee = new Employee(e.getEmployeeDTO());
    }
}
