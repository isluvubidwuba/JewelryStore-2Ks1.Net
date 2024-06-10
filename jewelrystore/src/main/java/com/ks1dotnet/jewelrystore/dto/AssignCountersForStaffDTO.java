package com.ks1dotnet.jewelrystore.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignCountersForStaffDTO {
    private Integer id;
    private CounterDTO counterDTO;
    private AssignShiftForStaffDTO assignShiftForStaffDTO;
}
