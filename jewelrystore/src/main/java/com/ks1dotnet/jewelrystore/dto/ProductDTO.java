package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private int id;
    private String productCode;
    private String barCode;
    private String name;
    private Double fee;
    private boolean status;
    private Float weight;
    private String img;
    private MaterialDTO materialDTO;
    private ProductCategoryDTO productCategoryDTO;
    private CounterDTO counterDTO;

}
