package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.PolicyForInvoice;

import jakarta.transaction.Transactional;

@Repository
public interface IPolicyForInvoiceRepository extends JpaRepository<PolicyForInvoice, Integer> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM policy_for_invoice WHERE id_invoice_type = :invoiceTypeId AND id_rate_policy = :ratePolicyId", nativeQuery = true)
    void deleteByExchangeRatePolicyAndInvoiceType(@Param("ratePolicyId") String idExchangeRate,
            @Param("invoiceTypeId") int invoiceType);

}
