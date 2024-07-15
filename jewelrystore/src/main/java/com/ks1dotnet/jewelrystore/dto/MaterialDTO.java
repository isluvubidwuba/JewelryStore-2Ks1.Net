package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private int id;
    private String name;
    private String purity;
    private double priceAtTime;
    private double priceBuyAtTime;
    private String lastModified;
}
