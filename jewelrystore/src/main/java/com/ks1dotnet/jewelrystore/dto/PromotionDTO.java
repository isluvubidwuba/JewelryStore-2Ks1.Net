package com.ks1dotnet.jewelrystore.dto;

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
    private VoucherTypeDTO voucherTypeDTO;
    private boolean status;
    private String image;

    public PromotionDTO(int id, String name, double value, int voucherTypeId, String voucherTypeName, boolean status,
            String image) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.voucherTypeDTO = new VoucherTypeDTO(voucherTypeId, voucherTypeName);
        this.status = status;
        this.image = image;
    }
}
