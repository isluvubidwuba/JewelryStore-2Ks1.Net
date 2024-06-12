package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForGemStoneType;
import com.ks1dotnet.jewelrystore.entity.GemStoneType;

@Repository
public interface IForGemStoneTypeRepository extends JpaRepository<ForGemStoneType, Integer> {
        @Query("SELECT f FROM ForGemStoneType f WHERE f.promotion.id = :promotionId")
        List<ForGemStoneType> findByPromotionId(@Param("promotionId") int promotionId);

        @Query("SELECT gt FROM GemStoneType gt WHERE gt.id NOT IN (SELECT fgt.gemstoneType.id FROM ForGemStoneType fgt WHERE fgt.promotion.id = :promotionId)")
        List<GemStoneType> findGemStoneTypesNotInPromotion(@Param("promotionId") int promotionId);

        @Query("SELECT fg FROM ForGemStoneType fg WHERE fg.promotion.id = :promotionId AND fg.gemstoneType.id = :gemstoneTypeId")
        ForGemStoneType findByPromotionIdAndGemStoneTypeId(@Param("promotionId") int promotionId,
                        @Param("gemstoneTypeId") int gemstoneTypeId);

        @Query("SELECT fg FROM ForGemStoneType fg WHERE fg.promotion.id = :promotionId AND fg.gemstoneType.id IN :gemstoneTypeIds")
        List<ForGemStoneType> findByPromotionIdAndGemStoneTypeIds(@Param("promotionId") int promotionId,
                        @Param("gemstoneTypeIds") List<Integer> gemstoneTypeIds);

        @Query("SELECT fg FROM ForGemStoneType fg JOIN fg.promotion p WHERE fg.gemstoneType.id = :gemstoneTypeId AND fg.status = true AND p.status = true AND p.promotionType = 'gemstone' AND p.invoiceType.id = :invoiceTypeId")
        List<ForGemStoneType> findActiveGemStoneTypePromotionsByGemStoneTypeIdAndInvoiceTypeId(
                        @Param("gemstoneTypeId") int gemstoneTypeId, @Param("invoiceTypeId") int invoiceTypeId);

}
