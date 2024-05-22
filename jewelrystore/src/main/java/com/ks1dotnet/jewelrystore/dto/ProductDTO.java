package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String id;
    private String name;
    private double fee;
    private boolean status;
    private MaterialOfProductDTO materialOfProductDTO;
    private ProductCategoryDTO productCategoryDTO;
    private CounterDTO counterDTO;

}
