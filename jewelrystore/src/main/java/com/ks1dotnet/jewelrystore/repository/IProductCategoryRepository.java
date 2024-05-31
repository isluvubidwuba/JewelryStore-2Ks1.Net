package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ProductCategory;

@Repository
public interface IProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
    @Query("SELECT pc FROM ProductCategory pc WHERE pc.id NOT IN :categoryIds")
    List<ProductCategory> findByIdNotIn(@Param("categoryIds") List<Integer> categoryIds);
}
