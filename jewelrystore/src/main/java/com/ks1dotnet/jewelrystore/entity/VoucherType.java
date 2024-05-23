package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voucher_type")
public class VoucherType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "type")
    private Integer type;

    @OneToMany(mappedBy = "voucherType")
    Set<Promotion> listPromotion;

    @OneToMany(mappedBy = "voucherType")
    Set<ForCustomer> listCustomers;

    @OneToMany(mappedBy = "voucherType")
    Set<ForProductType> listForProductType;

}
