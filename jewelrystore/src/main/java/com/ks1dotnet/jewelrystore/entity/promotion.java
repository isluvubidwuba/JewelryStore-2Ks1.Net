package com.ks1dotnet.jewelrystore.entity;

import java.util.HashSet;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "promotion")
public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private double value;
    @Column(name = "status")
    private boolean status;
    @Column(name = "image")
    private String image;

    @ManyToOne
    @JoinColumn(name = "id_voucher_type")
    private VoucherType voucherType;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ForProduct> listForProduct = new HashSet<>();

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<VoucherOnInvoice> listVoucherOnInvoice = new HashSet<>();

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail = new HashSet<>();

    public PromotionDTO getDTO() {
        return new PromotionDTO(this.id, this.name, this.value, this.voucherType.getDTO(), this.status, this.image);
    }

    public Promotion(PromotionDTO p) {
        this.id = p.getId();
        this.name = p.getName();
        this.value = p.getValue();
        this.status = p.isStatus();
        this.image = p.getImage();
        this.voucherType = new VoucherType(p.getVoucherTypeDTO());
    }

    public Promotion(int id) {
        this.id = id;
    }
}
