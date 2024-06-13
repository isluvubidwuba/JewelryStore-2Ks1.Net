package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.CustomerType;

@Repository
public interface ICustomerTypeRepository extends JpaRepository<CustomerType, Integer> {
    @Query("SELECT ct FROM CustomerType ct WHERE ct.id NOT IN :customerTypeIds")
    List<CustomerType> findByIdNotIn(@Param("customerTypeIds") List<Integer> customerTypeIds);
}

