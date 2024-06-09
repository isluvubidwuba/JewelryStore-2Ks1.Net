package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.WareHouse;

@Repository
public interface IWareHouseRepository extends JpaRepository<WareHouse, Integer> {

    @Query("SELECT w FROM WareHouse w WHERE w.product.id = :productId")
    WareHouse findByProductId(@Param("productId") int productId);

    @Modifying
    @Query("UPDATE WareHouse w SET w.quantity = :quantity, w.total_sold = :totalSold WHERE w.id = :id")
    void updateWareHouse(@Param("id") int id, @Param("quantity") int quantity, @Param("totalSold") int totalSold);

    @Query("SELECT w.quantity FROM WareHouse w WHERE w.product.id = :productId")
    int findQuantityByProductId(@Param("productId") int productId);
}
