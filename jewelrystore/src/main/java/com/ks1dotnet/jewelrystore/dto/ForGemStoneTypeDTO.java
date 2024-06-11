package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForGemStoneTypeDTO {
    private Integer id;
    private PromotionDTO promotionDTO;
    private GemStoneTypeDTO gemStoneTypeDTO;
    private boolean status;
}
