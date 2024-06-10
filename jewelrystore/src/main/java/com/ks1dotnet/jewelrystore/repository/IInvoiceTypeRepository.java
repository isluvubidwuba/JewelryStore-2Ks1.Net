package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;

@Repository
public interface IInvoiceTypeRepository extends JpaRepository<InvoiceType, Integer> {

}
