package com.ks1dotnet.jewelrystore.payload.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    private Integer userId;
    private String employeeId;
    private Map<Integer, Integer> products; // key: productId, value: quantity
}