package com.ks1dotnet.jewelrystore.entity;

import java.util.List;
import java.util.stream.Collectors;

import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "invoice_detail")
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "price")
    private Double price;

    @Column(name = "price_material_at_time")
    private Double priceMaterialAtTime;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "available_return_quantity")
    private double availableReturnQuantity;

    @ManyToOne
    @JoinColumn(name = "id_invoice")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_counter_at_time")
    private Counter counter;

    @OneToMany(mappedBy = "orderInvoiceDetail")
    List<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;

    public InvoiceDetailDTO getDTO() {
        InvoiceDetailDTO invoiceDetailDTO = new InvoiceDetailDTO();
        invoiceDetailDTO.setId(id);
        invoiceDetailDTO.setProductDTO(product.getDTO());
        invoiceDetailDTO.setQuantity(quantity);
        invoiceDetailDTO.setPrice(price);
        invoiceDetailDTO.setTotalPrice(totalPrice);
        invoiceDetailDTO.setAvailableReturnQuantity(availableReturnQuantity);
        List<PromotionDTO> lPromotionDTOs = listVoucherOnInvoiceDetail.stream()
                .map(voucherOnInvoiceDetail -> voucherOnInvoiceDetail.getPromotion().getDTO())
                .collect(Collectors.toList());
        invoiceDetailDTO.setListPromotion(lPromotionDTOs);
        return invoiceDetailDTO;
    }

}
