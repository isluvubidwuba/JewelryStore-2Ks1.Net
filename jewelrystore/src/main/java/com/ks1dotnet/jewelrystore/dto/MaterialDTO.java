package com.ks1dotnet.jewelrystore.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDTO {
    private Integer id;
    private String name;
    private String purity;
    private Double priceAtTime;
    private Date lastModified;
}
