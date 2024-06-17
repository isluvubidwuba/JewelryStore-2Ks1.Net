package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Inventory;

@Repository
public interface IInventoryRepository extends JpaRepository<Inventory, Integer> {
}
