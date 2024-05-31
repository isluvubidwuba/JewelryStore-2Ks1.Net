package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.VoucherTypeDTO;

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
    private String type;

    @OneToMany(mappedBy = "voucherType")
    Set<Promotion> listPromotion;

    @OneToMany(mappedBy = "voucherType")
    Set<ForCustomer> listCustomers;

    @OneToMany(mappedBy = "voucherType")
    Set<ForProductType> listForProductType;

    public VoucherTypeDTO getDTO() {
        return new VoucherTypeDTO(id, type);
    }

    public VoucherType(VoucherTypeDTO voucherTypeDTO) {
        this.id = voucherTypeDTO.getId();
        this.type = voucherTypeDTO.getType();
    }
}