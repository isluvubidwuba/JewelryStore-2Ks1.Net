package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GemStoneOfProductDTO {
    private Integer id;
    private String color;
    private String clarity;
    private Float carat;
    private Double price;
    private GemStoneTypeDTO gemstoneType;
    private GemStoneCategoryDTO gemstoneCategory;
    private ProductDTO product;
}
