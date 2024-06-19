package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRevenueDTO {
    private EmployeeDTO employee;
    private double totalRevenue;
    private double totalRevenueAfterDiscount;
    private int totalInvoices;
    private double averageRevenue;
}
