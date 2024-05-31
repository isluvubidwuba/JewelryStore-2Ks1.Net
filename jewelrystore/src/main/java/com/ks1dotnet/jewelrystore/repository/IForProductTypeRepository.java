package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.ForProductType;
import com.ks1dotnet.jewelrystore.entity.ProductCategory;
import com.ks1dotnet.jewelrystore.entity.VoucherType;

@Repository
public interface IForProductTypeRepository extends JpaRepository<ForProductType, Integer> {
    @Query("SELECT f.productCategory FROM ForProductType f WHERE f.voucherType.id = :voucherTypeId")
    List<ProductCategory> findCategoriesByVoucherTypeId(Integer voucherTypeId);

    @Query("SELECT fpt FROM ForProductType fpt WHERE fpt.voucherType = :voucherType AND fpt.productCategory.id IN :categoryIds")
    List<ForProductType> findAllByVoucherTypeAndCategoryIds(@Param("voucherType") VoucherType voucherType,
            @Param("categoryIds") List<Integer> categoryIds);

    @Query("SELECT fpt FROM ForProductType fpt WHERE fpt.voucherType = :voucherType")
    List<ForProductType> findByVoucherType(@Param("voucherType") VoucherType voucherType);
}
