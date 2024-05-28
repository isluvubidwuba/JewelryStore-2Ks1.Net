package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Table(name = "employee")
public class Employee {
    @Id
    private String id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "status")
    private boolean status;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

    @OneToMany(mappedBy = "employee")
    Set<AssignShiftForStaff> ListAssignShiftForStaff;

    @OneToMany(mappedBy = "employee")
    Set<OrderInvoice> listOrderInvoice;

    public EmployeeDTO getDTO(){
        return new EmployeeDTO(id, firstName, lastName, pinCode, status, phoneNumber, email, address, role.getDTO(), image);
    }
}