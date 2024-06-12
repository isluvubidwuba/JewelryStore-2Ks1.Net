package com.ks1dotnet.jewelrystore.dto;

import java.time.LocalDate;
import java.util.Date;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignShiftForStaffDTO {
    private Integer id;
    private Date date;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean isLate;
    private String note;
    private EmployeeDTO employeeDTO;
}
