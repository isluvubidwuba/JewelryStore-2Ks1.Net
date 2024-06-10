package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForProductDTO {
    private Integer Id;
    private PromotionDTO promotionDTO;
    private ProductDTO productDTO;
    private boolean status;
}
