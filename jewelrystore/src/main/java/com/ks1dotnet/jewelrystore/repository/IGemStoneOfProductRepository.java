package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.GemStoneOfProduct;

@Repository
public interface IGemStoneOfProductRepository extends JpaRepository<GemStoneOfProduct, Integer> {
    @Query("SELECT u FROM GemStoneOfProduct u WHERE u.product.id = :id")
    public List<GemStoneOfProduct> findGemStonesByProductId(@Param("id") int id);

}
