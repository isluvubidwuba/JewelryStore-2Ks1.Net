package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDTO {
    private int id;
    private String firstName;
    private String lastName;
    private String pinCode;
    private boolean status;
    private String phoneNumber;
    private String email;
    private String address;
    private RoleDTO role;
    private String image;
}
