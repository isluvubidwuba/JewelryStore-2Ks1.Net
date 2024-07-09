package com.ks1dotnet.jewelrystore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.EarnPoints;

@Repository
public interface IEarnPointsRepository extends JpaRepository<EarnPoints, Integer> {

    @Query("SELECT e FROM EarnPoints e WHERE e.userInfo.id = :customerId")
    public Optional<EarnPoints> findByCustomerId(@Param("customerId") Integer customerId);

    @Query("SELECT e FROM EarnPoints e WHERE e.customerType.id = :customerTypeId")
    public List<EarnPoints> findByCustomerTypeId(@Param("customerTypeId") Integer customerTypeId);

}
