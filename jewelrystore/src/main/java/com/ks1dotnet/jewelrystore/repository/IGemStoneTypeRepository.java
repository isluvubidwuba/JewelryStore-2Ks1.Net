package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.GemStoneType;

@Repository
public interface IGemStoneTypeRepository extends JpaRepository<GemStoneType, Integer> {
    @Query("SELECT gt.id, SUM(g.price * g.quantity) as total_value FROM GemStoneType gt LEFT JOIN GemStoneOfProduct g ON gt.id = g.gemstoneType.id WHERE g.product.id = :productId GROUP BY gt.id")
    List<Object[]> findGemstoneValuesByProductId(@Param("productId") int productId);
}
