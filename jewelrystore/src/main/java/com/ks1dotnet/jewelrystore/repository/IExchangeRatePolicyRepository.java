package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;

import jakarta.transaction.Transactional;

@Repository
public interface IExchangeRatePolicyRepository extends JpaRepository<ExchangeRatePolicy, String> {
    @Transactional
    @Modifying
    @Query("UPDATE ExchangeRatePolicy e SET e.status = false WHERE e.id = :id")
    void deactivatePolicyById(@Param("id") String id);

    @Query("SELECT e FROM ExchangeRatePolicy e WHERE e.invoiceType.id = :invoiceTypeId")
    List<ExchangeRatePolicy> findAllByInvoiceTypeId(@Param("invoiceTypeId") Integer invoiceTypeId);

}
