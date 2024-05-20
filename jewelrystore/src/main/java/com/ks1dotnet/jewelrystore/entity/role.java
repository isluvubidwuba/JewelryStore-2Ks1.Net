package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.*;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter@Setter@ToString
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @OneToMany(mappedBy = "role")
    Set<UserInfo> listUserInfo;

    @OneToMany(mappedBy = "role")
    Set<Employee> listEmployee;

}
