package com.ks1dotnet.jewelrystore.dto;

import lombok.Data;

@Data
public class PromotionDTO {
    private String name;
    private double value;
    private int idVoucherType;
    private boolean status;
    private String image;
}
