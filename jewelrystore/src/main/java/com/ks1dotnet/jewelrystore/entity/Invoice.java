package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;
import com.ks1dotnet.jewelrystore.dto.PromotionDTO;

import jakarta.persistence.CascadeType;
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
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "date")
    private Date date;

    @Column(name = "total_price_raw")
    private double totalPriceRaw;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "discount_price")
    private double discountPrice;

    @Column(name = "status")
    private boolean status = true;

    @Column(name = "note", nullable = true)
    private String note;

    @Column(name = "payment")
    private String payment;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "invoice")
    List<InvoiceDetail> listOrderInvoiceDetail;

    public InvoiceDTO getDTO() {
        InvoiceDTO inpDto = new InvoiceDTO();
        inpDto.setId(id);
        inpDto.setDate(date);
        inpDto.setTotalPriceRaw(totalPriceRaw);
        inpDto.setTotalPrice(totalPrice);
        inpDto.setDiscountPrice(discountPrice);
        inpDto.setStatus(status);
        inpDto.setUserInfoDTO(userInfo.getDTO());
        inpDto.setEmployeeDTO(employee.getDTO());
        inpDto.setInvoiceTypeDTO(invoiceType.getDTO());
        inpDto.setPayment(payment);
        inpDto.setNote(note);
        List<PromotionDTO> listPromotionForInvoice = listVoucherOnInvoice.stream()
                .map(VoucherOnInvoice -> VoucherOnInvoice.getPromotion().getDTO())
                .collect(Collectors.toList());
        List<InvoiceDetailDTO> invoiceDetailDTOs = listOrderInvoiceDetail.stream()
                .map(InvoiceDetail::getDTO)
                .collect(Collectors.toList());
        inpDto.setListPromotionOnInvoice(listPromotionForInvoice);
        inpDto.setListOrderInvoiceDetail(invoiceDetailDTOs);
        return inpDto;
    }

}
