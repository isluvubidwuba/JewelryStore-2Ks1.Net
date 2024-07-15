package com.ks1dotnet.jewelrystore.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignShiftForStaffDTO {
    private Integer id;
    private Date date;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;
    private Boolean isLate;
    private EmployeeDTO employeeDTO;
}
