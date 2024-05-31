package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.Promotion;
import java.util.List;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {
        public List<Promotion> findByStatus(boolean status);

        public List<Promotion> findByNameLike(String name);

        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, v.id, v.type, p.status, p.image) "
                        +
                        "FROM Promotion p JOIN p.voucherType v WHERE p.id = :id")
        PromotionDTO findPromotionDTOById(@Param("id") int id);

        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, v.id, v.type, p.status, p.image) "
                        +
                        "FROM Promotion p JOIN p.voucherType v")
        Page<PromotionDTO> findAllPromotions(Pageable pageable);
}
