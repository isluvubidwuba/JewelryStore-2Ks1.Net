package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.AssignCountersForStaffDTO;

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
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "assign_counters_for_staff")
public class AssignCountersForStaff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_counter")
    private Counter counter;

    @ManyToOne
    @JoinColumn(name = "id_assign")
    private AssignShiftForStaff assignShiftForStaff;

    public AssignCountersForStaffDTO getDTO() {
        return new AssignCountersForStaffDTO(this.id, this.counter.getDTO(), this.assignShiftForStaff.getDTO());
    }

    public AssignCountersForStaff(AssignCountersForStaffDTO e) {
        this.id = e.getId();
        this.counter = new Counter(e.getCounterDTO());
        this.assignShiftForStaff = new AssignShiftForStaff(e.getAssignShiftForStaffDTO());
    }

}
