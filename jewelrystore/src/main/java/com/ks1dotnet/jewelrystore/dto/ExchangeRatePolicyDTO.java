package com.ks1dotnet.jewelrystore.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeRatePolicyDTO {
    private String id;
    private String description_policy;
    private float rate;
    private boolean status;
    private LocalDate lastModified;

    public void setLastModified() {
        this.lastModified = LocalDate.now();
    }
}
