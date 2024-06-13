package com.ks1dotnet.jewelrystore.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.dto.PromotionDTO;
import com.ks1dotnet.jewelrystore.entity.InvoiceType;
import com.ks1dotnet.jewelrystore.entity.Promotion;

@Repository
public interface IPromotionRepository extends JpaRepository<Promotion, Integer> {

        List<Promotion> findByInvoiceTypeAndStatusTrue(InvoiceType invoiceType);

        @Query("SELECT new com.ks1dotnet.jewelrystore.dto.PromotionDTO(p.id, p.name, p.value, p.status, p.image, p.startDate, p.endDate, p.lastModified, p.promotionType, p.invoiceType) FROM Promotion p")
        List<PromotionDTO> findAllPromotions2();

        List<Promotion> findByEndDateBefore(LocalDate date);

        @Query("SELECT p FROM Promotion p " +
                        "WHERE p.invoiceType.id = :idInvoiceType " +
                        "AND (EXISTS (" +
                        "        SELECT fp.promotion.id " +
                        "        FROM ForProduct fp " +
                        "        WHERE fp.status = true " +
                        "        AND fp.product.id = :idProduct " +
                        "        AND fp.promotion.id = p.id " +
                        "    ) " +
                        "    OR EXISTS (" +
                        "        SELECT fpt.promotion.id " +
                        "        FROM ForProductType fpt " +
                        "        WHERE fpt.status = true " +
                        "        AND fpt.productCategory.id = :idCategory " +
                        "        AND fpt.promotion.id = p.id " +
                        "    ) " +
                        "    OR EXISTS (" +
                        "        SELECT fm.promotion.id " +
                        "        FROM ForMaterial fm " +
                        "        WHERE fm.status = true " +
                        "        AND fm.material.id = (SELECT prod.material.id FROM Product prod WHERE prod.id = :idProduct) "
                        +
                        "        AND fm.promotion.id = p.id " +
                        "    ) " +
                        "    OR EXISTS (" +
                        "        SELECT fgt.promotion.id " +
                        "        FROM ForGemStoneType fgt " +
                        "        WHERE fgt.status = true " +
                        "        AND fgt.gemstoneType.id IN (SELECT gop.gemstoneType.id FROM GemStoneOfProduct gop WHERE gop.product.id = :idProduct) "
                        +
                        "        AND fgt.promotion.id = p.id " +
                        "    ))")
        List<Promotion> findPromotionsByCriteria(@Param("idInvoiceType") int idInvoiceType,
                        @Param("idProduct") int idProduct,
                        @Param("idCategory") int idCategory);

        @Query("SELECT p FROM Promotion p JOIN ForGemStoneType fgt ON p.id = fgt.promotion.id WHERE fgt.gemstoneType.id = :gemstoneTypeId AND p.promotionType = 'gemstone'")
        List<Promotion> findPromotionsForGemstoneType(@Param("gemstoneTypeId") int gemstoneTypeId);
}
