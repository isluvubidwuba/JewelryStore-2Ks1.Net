package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.repository.IInvoiceRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @Column(name = "id")
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

    @Column(name = "otp")
    private String otp;

    @Column(name = "otp_generated_time")
    private LocalDateTime otpGenerDateTime;

    @Column(name = "token")
    private String token;

    @ManyToOne
    @JoinColumn(name = "id_role")
    private Role role;

    @OneToMany(mappedBy = "employee")
    Set<AssignShiftForStaff> ListAssignShiftForStaff;

    @OneToMany(mappedBy = "employee")
    Set<Invoice> listOrderInvoice;

    public EmployeeDTO getDTO() {
        return new EmployeeDTO(id, firstName, lastName, pinCode, status, phoneNumber, email,
                address, role.getDTO(), image, 0);
    }

    public Employee(EmployeeDTO e) {
        this.id = e.getId();
        this.firstName = e.getFirstName();
        this.lastName = e.getLastName();
        this.pinCode = e.getPinCode();
        this.status = e.isStatus();
        this.phoneNumber = e.getPhoneNumber();
        this.email = e.getEmail();
        this.address = e.getAddress();
        this.role = new Role(e.getRole());
        this.image = e.getImage();
    }

}
