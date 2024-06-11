package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForMaterialDTO {
    private Integer id;
    private PromotionDTO promotionDTO;
    private MaterialDTO materialDTO;
    private boolean status;
}
