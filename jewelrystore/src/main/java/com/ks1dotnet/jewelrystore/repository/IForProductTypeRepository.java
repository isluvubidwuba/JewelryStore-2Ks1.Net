package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForProductType;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;

@Repository
public interface IForProductTypeRepository extends JpaRepository<ForProductType, Integer> {

        @Query("SELECT f FROM ForProductType f WHERE f.promotion.id = :promotionId")
        List<ForProductType> findByPromotionId(@Param("promotionId") int promotionId);

        @Query("SELECT pc FROM ProductCategory pc WHERE pc.id NOT IN (SELECT fpt.productCategory.id FROM ForProductType fpt WHERE fpt.promotion.id = :promotionId)")
        List<ProductCategory> findProductCategoriesNotInPromotion(@Param("promotionId") int promotionId);

        @Query("SELECT f FROM ForProductType f WHERE f.promotion.id = :promotionId AND f.productCategory.id = :productCategoryId")
        ForProductType findByPromotionIdAndProductCategoryId(@Param("promotionId") int promotionId,
                        @Param("productCategoryId") int productCategoryId);

        @Query("SELECT f FROM ForProductType f WHERE f.promotion.id = :promotionId AND f.productCategory.id IN :productCategoryIds")
        List<ForProductType> findByPromotionIdAndProductCategoryIds(@Param("promotionId") int promotionId,
                        @Param("productCategoryIds") List<Integer> productCategoryIds);

        @Query("SELECT f FROM ForProductType f JOIN f.promotion p WHERE f.productCategory.id = :productCategoryId AND f.status = true AND p.status = true AND p.promotionType = 'category' AND p.invoiceType.id = :invoiceTypeId")
        List<ForProductType> findActiveProductTypePromotionsByProductCategoryIdAndInvoiceTypeId(
                        @Param("productCategoryId") int productCategoryId, @Param("invoiceTypeId") int invoiceTypeId);
}
