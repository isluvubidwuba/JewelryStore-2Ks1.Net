package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.UserInfoDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "user_info")
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "full_name")
    private String fullName;

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

    @OneToMany(mappedBy = "userInfo")
    Set<EarnPoints> listEarnPoints;

    @OneToMany(mappedBy = "userInfo")
    Set<Invoice> listOrderInvoice;

    public UserInfoDTO getDTO() {
        return new UserInfoDTO(id, fullName, phoneNumber, email, address, role.getDTO(), image);
    }

    public UserInfo(UserInfoDTO t) {
        this.id = t.getId();
        this.fullName = t.getFullName();
        this.phoneNumber = t.getPhoneNumber();
        this.email = t.getEmail();
        this.address = t.getAddress();
        this.role = new Role(t.getRole());
        this.image = t.getImage();
    }

}