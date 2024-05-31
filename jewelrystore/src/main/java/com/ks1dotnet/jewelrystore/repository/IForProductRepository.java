package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForProduct;
import com.ks1dotnet.jewelrystore.entity.Product;

import jakarta.transaction.Transactional;

@Repository
public interface IForProductRepository extends JpaRepository<ForProduct, Integer> {
    @Query("SELECT fp.product FROM ForProduct fp WHERE fp.promotion.id = :promotionId")
    List<Product> findProductsByPromotionId(@Param("promotionId") int promotionId);

    @Query("SELECT p FROM Product p WHERE p.id NOT IN (SELECT fp.product.id FROM ForProduct fp WHERE fp.promotion.id = :promotionId)")
    List<Product> findProductsNotInPromotion(@Param("promotionId") int promotionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ForProduct fp WHERE fp.promotion.id = :promotionId AND fp.product.id IN :productIds")
    void deleteByPromotionIdAndProductIds(@Param("promotionId") int promotionId,
            @Param("productIds") List<Integer> productIds);
}
