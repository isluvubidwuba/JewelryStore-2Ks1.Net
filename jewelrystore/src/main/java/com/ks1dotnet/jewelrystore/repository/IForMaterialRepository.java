package com.ks1dotnet.jewelrystore.repository;

import com.ks1dotnet.jewelrystore.entity.ForMaterial;
import com.ks1dotnet.jewelrystore.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IForMaterialRepository extends JpaRepository<ForMaterial, Integer> {

    @Query("SELECT f FROM ForMaterial f WHERE f.promotion.id = :promotionId")
    List<ForMaterial> findByPromotionId(@Param("promotionId") int promotionId);

    @Query("SELECT m FROM Material m WHERE m.id NOT IN (SELECT fm.material.id FROM ForMaterial fm WHERE fm.promotion.id = :promotionId)")
    List<Material> findMaterialsNotInPromotion(@Param("promotionId") int promotionId);

    @Query("SELECT fm FROM ForMaterial fm WHERE fm.promotion.id = :promotionId AND fm.material.id = :materialId")
    ForMaterial findByPromotionIdAndMaterialId(@Param("promotionId") int promotionId,
            @Param("materialId") int materialId);

    @Query("SELECT fm FROM ForMaterial fm WHERE fm.promotion.id = :promotionId AND fm.material.id IN :materialIds")
    List<ForMaterial> findByPromotionIdAndMaterialIds(@Param("promotionId") int promotionId,
            @Param("materialIds") List<Integer> materialIds);

    @Query("SELECT fm FROM ForMaterial fm JOIN fm.promotion p WHERE fm.material.id = :materialId AND fm.status = true AND p.status = true AND p.promotionType = 'material' AND p.invoiceType.id = :invoiceTypeId")
    List<ForMaterial> findActiveMaterialPromotionsByMaterialIdAndInvoiceTypeId(@Param("materialId") int materialId,
            @Param("invoiceTypeId") int invoiceTypeId);
}
