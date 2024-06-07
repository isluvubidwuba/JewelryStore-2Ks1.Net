package com.ks1dotnet.jewelrystore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;

public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {

    @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType) FROM Promotion p WHERE p.id = :id")
    PromotionDTO findPromotionDTOById(@Param("id") int id);

    @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType) FROM Promotion p")
    Page<PromotionDTO> findAllPromotions(Pageable pageable);

    List<Promotion> findByEndDateBefore(LocalDate date);
}
