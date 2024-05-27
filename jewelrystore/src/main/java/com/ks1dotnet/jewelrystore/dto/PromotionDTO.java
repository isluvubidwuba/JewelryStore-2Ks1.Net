package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private int id;
    private String name;
    private double value;
    private int idVoucherType;
    private boolean status;
    private String image;
}
