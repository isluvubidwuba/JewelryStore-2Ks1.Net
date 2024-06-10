package com.ks1dotnet.jewelrystore.entity;

import java.util.Set;

import com.ks1dotnet.jewelrystore.dto.CounterDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "counter")
public class Counter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private boolean status;

    @OneToMany(mappedBy = "counter")
    Set<AssignCountersForStaff> ListAssignCountersForStaff;

    @OneToMany(mappedBy = "counter")
    Set<Product> listProduct;

    @OneToMany(mappedBy = "counter")
    Set<OrderInvoiceDetail> listOrderInvoiceDetail;

    public CounterDTO getDTO() {
        return new CounterDTO(this.id, this.name, this.status);
    }

    public Counter(CounterDTO t) {
        this.id = t.getId();
        this.name = t.getName();
        this.status = t.isStatus();
    }
}
