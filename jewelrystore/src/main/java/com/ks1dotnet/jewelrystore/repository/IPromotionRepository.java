package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Promotion;
import java.util.List;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {
    public List<Promotion> findByStatus(boolean status);

    public List<Promotion> findByNameLike(String name);

}
