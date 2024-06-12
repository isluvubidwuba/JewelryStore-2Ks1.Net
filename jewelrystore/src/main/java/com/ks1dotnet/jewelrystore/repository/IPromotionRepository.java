package com.ks1dotnet.jewelrystore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {

        // @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name,
        // p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified,
        // p.promotionType, p.invoiceType) FROM Promotion p")
        // Page<PromotionDTO> findAllPromotions(Pageable pageable);

        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType, p.invoiceType) FROM Promotion p")
        List<PromotionDTO> findAllPromotions2();

        List<Promotion> findByEndDateBefore(LocalDate date);

        // sử dụng để lấy PromotionDTO by Product ID
        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType) "
                        +
                        "FROM Promotion p " +
                        "JOIN p.listForProduct fp " +
                        "JOIN fp.product pr " +
                        "WHERE pr.id = :productId " +
                        "AND p.status = true " +
                        "AND fp.status = true")
        List<PromotionDTO> findPromotionsByProductId(@Param("productId") int productId);

        // sử dụng để lấy PromotionDTO by category của product id
        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType) "
                        +
                        "FROM Promotion p " +
                        "JOIN p.listForProductType fpt " +
                        "JOIN fpt.productCategory pc " +
                        "WHERE pc.id = :productCategoryId " +
                        "AND p.status = true " +
                        "AND fpt.status = true")
        List<PromotionDTO> findPromotionsByProductCategoryId(@Param("productCategoryId") int productCategoryId);
}
