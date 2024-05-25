package com.ks1dotnet.jewelrystore.dto;

import com.ks1dotnet.jewelrystore.entity.InvoiceType;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvoiceTypeDTO {
    private int id;
    private String name;

}
