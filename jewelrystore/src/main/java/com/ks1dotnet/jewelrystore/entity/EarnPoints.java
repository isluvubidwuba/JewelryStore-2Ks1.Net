package com.ks1dotnet.jewelrystore.entity;

import com.ks1dotnet.jewelrystore.dto.EarnPointsDTO;

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
@Table(name = "earn_points")
public class EarnPoints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "point")
    private Integer point;

    @ManyToOne
    @JoinColumn(name = "id_customer_info")
    private UserInfo userInfo;

    @ManyToOne
    @JoinColumn(name = "id_customer_type")
    private CustomerType customerType;

    public EarnPointsDTO getDTO(EarnPointsDTO e) {
        return new EarnPointsDTO(this.id, this.point, this.userInfo.getDTO(), this.customerType.getDTO());
    }

    public EarnPoints(EarnPointsDTO t) {
        this.id = t.getId();
        this.point = t.getPoint();
        this.userInfo = new UserInfo(t.getUserInfoDTO());
        this.customerType = new CustomerType(t.getCustomerTypeDTO());
    }

}
