package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.ForCustomerDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "for_customer")
public class ForCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "status")
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_customer_type")
    private CustomerType customerType;

    public ForCustomer(Promotion promotion, CustomerType customerType, boolean status) {
        this.promotion = promotion;
        this.customerType = customerType;
        this.status = status;
    }

    public ForCustomerDTO getDTO() {
        return new ForCustomerDTO(this.id, this.promotion.getDTO(), this.customerType.getDTO(), this.isStatus());
    }
}
