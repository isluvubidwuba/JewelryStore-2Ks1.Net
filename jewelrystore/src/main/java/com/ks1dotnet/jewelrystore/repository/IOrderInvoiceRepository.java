package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.OrderInvoice;

@Repository
public interface IOrderInvoiceRepository extends JpaRepository<OrderInvoice, Integer> {
    @Query("SELECT p.id, p.name, " +
            "(COALESCE(SUM(g.price * g.quantity), 0) + p.fee + (m.priceAtTime * p.weight)) AS actual_price, " +
            "w.quantity " +
            "FROM Product p " +
            "LEFT JOIN p.listGemStoneOfProduct g " +
            "JOIN p.material m " +
            "JOIN p.wareHouse w " +
            "WHERE p.barCode = :barcode " +
            "GROUP BY p.id, p.name, p.fee, m.priceAtTime, p.weight, w.quantity")
    Object getProductActualPriceByBarcode(@Param("barcode") String barcode);
}
