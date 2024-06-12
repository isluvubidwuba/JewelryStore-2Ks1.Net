package com.ks1dotnet.jewelrystore.dto;

import org.springframework.beans.factory.annotation.Value;

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
    private double fee;
    private boolean status;
    private float weight;
    private MaterialDTO materialDTO;
    private ProductCategoryDTO productCategoryDTO;
    private CounterDTO counterDTO;
    private String imgPath;

}
