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

        @Query("SELECT fp FROM ForProductType fp WHERE fp.promotion.id = :promotionId")
        List<ForProductType> findCategoriesByPromotionId(@Param("promotionId") int promotionId);

        @Query("SELECT fp FROM ForProductType fp WHERE fp.promotion.id = :promotionId AND fp.productCategory.id = :categoryId")
        ForProductType findByPromotionIdAndCategoryId(@Param("promotionId") int promotionId,
                        @Param("categoryId") int categoryId);

        @Query("SELECT fp FROM ForProductType fp WHERE fp.promotion.id = :promotionId AND fp.productCategory.id IN :categoryIds")
        List<ForProductType> findByPromotionIdAndCategoryIds(@Param("promotionId") int promotionId,
                        @Param("categoryIds") List<Integer> categoryIds);

        @Query("SELECT fp FROM ForProductType fp JOIN fp.promotion p WHERE fp.productCategory.id = :categoryId AND fp.status = true AND p.status = true AND p.promotionType = 'category'")
        List<ForProductType> findActiveCategoryPromotionsByCategoryId(@Param("categoryId") int categoryId);

        @Query("SELECT pc FROM ProductCategory pc WHERE pc.id NOT IN (SELECT fpt.productCategory.id FROM ForProductType fpt WHERE fpt.promotion.id = :promotionId)")
        List<ProductCategory> findCategoriesNotInPromotion(@Param("promotionId") int promotionId);
}
