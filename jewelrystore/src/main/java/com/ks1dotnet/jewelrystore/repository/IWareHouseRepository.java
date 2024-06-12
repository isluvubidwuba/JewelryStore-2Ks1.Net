package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Invoice_Detail_Import;

@Repository
public interface IWareHouseRepository extends JpaRepository<Invoice_Detail_Import, Integer> {

}
