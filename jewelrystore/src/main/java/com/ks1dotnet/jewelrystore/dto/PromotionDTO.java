package com.ks1dotnet.jewelrystore.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromotionDTO {
    private int id;
    private String name;
    private double value;
    private boolean status;
    private String image;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate lastModified;
    private String promotionType;

    public void setLastModified() {
        this.lastModified = LocalDate.now();
    }
}
