package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.Enum.EntityType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplyPromotionDTO {
    private int promotionId;
    private List<Integer> entityIds;
    private EntityType entityType;
}
