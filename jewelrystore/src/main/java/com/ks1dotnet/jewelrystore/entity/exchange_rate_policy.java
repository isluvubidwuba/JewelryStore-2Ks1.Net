package com.ks1dotnet.jewelrystore.entity;

import java.util.Date;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "exchange_rate_policy")
public class exchange_rate_policy {
    @Id
    private String id;
    private String description_policy;
    private float rate;
    private boolean status;
    private Date last_modified;

    @OneToMany(mappedBy = "exchange_rate_policy")
    Set<policy_for_invoice> list_policy_for_invoice;
}
