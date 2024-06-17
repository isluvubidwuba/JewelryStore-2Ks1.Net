package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.VoucherOnInvoiceDTO;

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
@Table(name = "voucher_on_invoice")
public class VoucherOnInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_promotion")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "id_invoice")
    private Invoice invoice;

    public VoucherOnInvoice(Promotion promotion, Invoice orderInvoice) {
        this.promotion = promotion;
        this.invoice = orderInvoice;
    }

    public VoucherOnInvoiceDTO getDTO() {
        return new VoucherOnInvoiceDTO(id, promotion.getDTO(), invoice.getDTO());
    }
}
