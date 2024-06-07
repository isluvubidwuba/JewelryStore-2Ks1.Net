package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForCustomerDTO {
    private Integer id;
    private PromotionDTO promotionDTO;
    private CustomerTypeDTO customerTypeDTO;
    private boolean status;
}
