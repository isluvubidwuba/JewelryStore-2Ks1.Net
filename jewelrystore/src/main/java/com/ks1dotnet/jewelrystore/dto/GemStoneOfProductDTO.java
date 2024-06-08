package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GemStoneOfProductDTO {
    private int id;
    private String color;
    private String clarity;
    private float carat;
    private double price;
    private GemStoneTypeDTO gemstoneType;
    private GemStoneCategoryDTO gemstoneCategory;
    private ProductDTO product;
    private int quantity;
}
