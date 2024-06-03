package com.ks1dotnet.jewelrystore.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTypeDTO {
    private Integer id;
    private String type;
    private Integer pointCondition;
}
