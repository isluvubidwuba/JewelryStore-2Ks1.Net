package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.Product;

@Repository
public interface IForProductRepository extends JpaRepository<ForProduct, Integer> {

        @Query("SELECT f FROM ForProduct f WHERE f.promotion.id = :promotionId")
        List<ForProduct> findByPromotionId(@Param("promotionId") int promotionId);

        @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT fp.product.id FROM ForProduct fp WHERE fp.promotion.id = :promotionId)")
        List<Product> findProductsNotInPromotion(@Param("promotionId") int promotionId);

        @Query("SELECT f FROM ForProduct f WHERE f.promotion.id = :promotionId AND f.product.id = :productId")
        ForProduct findByPromotionIdAndProductId(@Param("promotionId") int promotionId,
                        @Param("productId") int productId);

        @Query("SELECT f FROM ForProduct f WHERE f.promotion.id = :promotionId AND f.product.id IN :productIds")
        List<ForProduct> findByPromotionIdAndProductIds(@Param("promotionId") int promotionId,
                        @Param("productIds") List<Integer> productIds);

        @Query("SELECT f FROM ForProduct f JOIN f.promotion p WHERE f.product.id = :productId AND f.status = true AND p.status = true AND p.promotionType = 'product' AND p.invoiceType.id = :invoiceTypeId")
        List<ForProduct> findActiveProductPromotionsByProductIdAndInvoiceTypeId(
                        @Param("productId") int productId, @Param("invoiceTypeId") int invoiceTypeId);
}
