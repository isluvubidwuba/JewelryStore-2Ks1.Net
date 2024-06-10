package com.ks1dotnet.jewelrystore.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.OrderInvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.ProductDTO;
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
@Table(name = "order_invoice_detail")
public class OrderInvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "price")
    private Double price;
    @Column(name = "price_material_at_time")
    private Double priceMaterialAtTime;
    @Column(name = "quantity")
    private int quantity;
    private double total_price;

    @ManyToOne
    @JoinColumn(name = "id_order_invoice")
    private OrderInvoice orderInvoice;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "id_counter_at_time")
    private Counter counter;

    @OneToMany(mappedBy = "orderInvoiceDetail")
    List<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;

    public OrderInvoiceDetailDTO getDTO() {
        List<PromotionDTO> listPromotion = new ArrayList<>();
        for (VoucherOnInvoiceDetail voucherOnInvoiceDetail : this.listVoucherOnInvoiceDetail) {
            listPromotion.add(voucherOnInvoiceDetail.getPromotion().getDTO());
        }
        return new OrderInvoiceDetailDTO(this.product.getDTO(), this.price, this.quantity, this.total_price,
                listPromotion);
    }

    // private ProductDTO productDTO;
    // private double price;
    // private int quantity;
    // private double totalPrice;
    // private List<PromotionDTO> listPromotion;
}
