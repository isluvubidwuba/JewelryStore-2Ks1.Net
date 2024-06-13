package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.ks1dotnet.jewelrystore.dto.InvoiceDTO;
import com.ks1dotnet.jewelrystore.dto.InvoiceDetailDTO;

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
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "id_employee")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @OneToMany(mappedBy = "invoice")
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
        List<InvoiceDetailDTO> invoiceDetailDTOs = listOrderInvoiceDetail.stream()
                .map(InvoiceDetail::getDTO)
                .collect(Collectors.toList());
        inpDto.setListOrderInvoiceDetail(invoiceDetailDTOs);
        return inpDto;
    }

}
