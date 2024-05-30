package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM Product p WHERE p.name = :name AND p.id_material = :idMaterial AND p.id_product_category = :idProductCategory AND p.id_counter = :idCounter AND p.fee = :fee", nativeQuery = true)
    List<Product> findByAllFieldsExceptId(
            @Param("name") String name,
            @Param("idMaterial") Integer idMaterial,
            @Param("idProductCategory") Integer idProductCategory,
            @Param("idCounter") Integer idCounter,
            @Param("fee") Double fee);

    @Query(value = "SELECT MAX(p.id) FROM Product p")
    Integer findMaxId();
}
