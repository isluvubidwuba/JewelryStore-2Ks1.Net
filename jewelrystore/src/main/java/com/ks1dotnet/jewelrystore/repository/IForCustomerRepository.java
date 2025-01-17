package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForCustomer;
import com.ks1dotnet.jewelrystore.entity.CustomerType;

@Repository
public interface IForCustomerRepository extends JpaRepository<ForCustomer, Integer> {

        @Query("SELECT f FROM ForCustomer f WHERE f.promotion.id = :promotionId")
        List<ForCustomer> findByPromotionId(@Param("promotionId") int promotionId);

        @Query("SELECT ct FROM CustomerType ct WHERE ct.id NOT IN (SELECT fc.customerType.id FROM ForCustomer fc WHERE fc.promotion.id = :promotionId)")
        List<CustomerType> findCustomerTypesNotInPromotion(@Param("promotionId") int promotionId);

        @Query("SELECT fc FROM ForCustomer fc WHERE fc.promotion.id = :promotionId AND fc.customerType.id = :customerTypeId")
        ForCustomer findByPromotionIdAndCustomerTypeId(@Param("promotionId") int promotionId,
                        @Param("customerTypeId") int customerTypeId);

        @Query("SELECT fc FROM ForCustomer fc WHERE fc.promotion.id = :promotionId AND fc.customerType.id IN :customerTypeIds")
        List<ForCustomer> findByPromotionIdAndCustomerTypeIds(@Param("promotionId") int promotionId,
                        @Param("customerTypeIds") List<Integer> customerTypeIds);

        @Query("SELECT fc FROM ForCustomer fc JOIN fc.promotion p WHERE fc.customerType.id = :customerTypeId AND fc.status = true AND p.status = true AND p.promotionType = 'customer' AND p.invoiceType.id = :invoiceTypeId")
        List<ForCustomer> findActiveCustomerTypePromotionsByCustomerTypeIdAndInvoiceTypeId(
                        @Param("customerTypeId") int customerTypeId, @Param("invoiceTypeId") int invoiceTypeId);

        @Query("SELECT fc FROM ForCustomer fc JOIN fc.promotion p JOIN fc.customerType ct JOIN ct.listEarnPoints ep WHERE ep.userInfo.id = :userId AND p.status = true AND fc.status = true")
        ForCustomer findActivePromotionsByUserId(@Param("userId") int userId);
}
