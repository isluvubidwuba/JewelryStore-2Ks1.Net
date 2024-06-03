package com.ks1dotnet.jewelrystore.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EarnPointsDTO {
    private Integer id;
    private Integer point;
    private UserInfoDTO userInfoDTO;
    private CustomerTypeDTO customerTypeDTO;
}
