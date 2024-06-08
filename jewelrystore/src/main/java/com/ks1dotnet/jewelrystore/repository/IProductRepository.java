package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Product;

@Repository
public interface IProductRepository extends JpaRepository<Product, Integer> {

        // @Query(value = "SELECT * FROM Product p WHERE p.name = :name AND
        // p.id_material = :idMaterial AND p.id_product_category = :idProductCategory
        // AND p.id_counter = :idCounter AND p.fee = :fee", nativeQuery = true)
        // List<Product> findByAllFieldsExceptId(
        // @Param("name") String name,
        // @Param("idMaterial") Integer idMaterial,
        // @Param("idProductCategory") Integer idProductCategory,
        // @Param("idCounter") Integer idCounter,
        // @Param("fee") Double fee);

        @Query(value = "SELECT MAX(p.id) FROM Product p")
        Integer findMaxId();

        // @Transactional
        // @Procedure(procedureName = "GET_DynamicSearch_Product")
        // List<Product> dynamicSearchProduct(String search, String id_material, String
        // id_product_category,
        // String id_counter);

        @Query(value = "SELECT * FROM Product p WHERE " +
                        "(:search IS NULL OR p.product_code LIKE %:search% OR p.name COLLATE SQL_Latin1_General_CP1_CI_AI LIKE %:search% OR p.barcode LIKE %:search% OR p.fee LIKE %:search% OR p.weight LIKE %:search% ) AND "
                        +
                        "(:id_material IS NULL OR p.id_material LIKE %:id_material%) AND " +
                        "(:id_product_category IS NULL OR p.id_product_category LIKE %:id_product_category%) AND " +
                        "(:id_counter IS NULL OR p.id_counter LIKE %:id_counter%)", nativeQuery = true)
        Page<Product> dynamicSearchProductV2(@Param("search") String search,
                        @Param("id_material") String id_material,
                        @Param("id_product_category") String id_product_category,
                        @Param("id_counter") String id_counter,
                        Pageable pageable);

}
