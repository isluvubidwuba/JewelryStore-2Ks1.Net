package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ExchangeRatePolicy;

@Repository
public interface IExchangeRatePolicyRepository extends JpaRepository<ExchangeRatePolicy, String> {
    @Query("SELECT e FROM ExchangeRatePolicy e WHERE e.id LIKE %:keyword% OR e.description_policy LIKE %:keyword%")
    List<ExchangeRatePolicy> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT e FROM ExchangeRatePolicy e JOIN e.listPolicyForInvoice p WHERE p.invoiceType.id = :invoiceTypeId")
    List<ExchangeRatePolicy> findByInvoiceTypeId(@Param("invoiceTypeId") int invoiceTypeId);

}
