package com.ks1dotnet.jewelrystore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class UserInfoDTO {
    private int id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private RoleDTO role;
    private String image;

}
