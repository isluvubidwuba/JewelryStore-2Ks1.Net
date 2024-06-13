package com.ks1dotnet.jewelrystore.entity;

import java.time.LocalDate;
import java.util.List;

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
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private double value;

    @Column(name = "status")
    private boolean status;

    @Column(name = "image")
    private String image;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "last_modified")
    private LocalDate lastModified;

    @Column(name = "promotion_type")
    private String promotionType;

    @ManyToOne
    @JoinColumn(name = "id_invoice_type")
    private InvoiceType invoiceType;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ForCustomer> listForCustomer;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ForProductType> listForProductType;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ForProduct> listForProduct;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VoucherOnInvoice> listVoucherOnInvoice;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<VoucherOnInvoiceDetail> listVoucherOnInvoiceDetail;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ForGemStoneType> listForGemStoneTypes;

    @OneToMany(mappedBy = "promotion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ForMaterial> listForMaterials;

    public PromotionDTO getDTO() {
        return new PromotionDTO(this.id, this.name, this.value, this.status, this.image, this.startDate, this.endDate,
                this.lastModified, this.promotionType, this.invoiceType.getDTO());
    }

    public Promotion(PromotionDTO p) {
        this.id = p.getId();
        this.name = p.getName();
        this.value = p.getValue();
        this.status = p.isStatus();
        this.image = p.getImage();
        this.startDate = p.getStartDate();
        this.endDate = p.getEndDate();
        this.lastModified = p.getLastModified();
        this.promotionType = p.getPromotionType();
    }

    public void setLastModified() {
        this.lastModified = LocalDate.now();
    }

    public Promotion(int id) {
        this.id = id;
    }
}
