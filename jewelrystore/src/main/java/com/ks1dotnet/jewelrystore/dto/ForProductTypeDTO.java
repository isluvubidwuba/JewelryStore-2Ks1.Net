package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForProductTypeDTO {
    private Integer Id;
    private VoucherTypeDTO voucherTypeDTO;
    private ProductCategoryDTO categoryDTO;
}
