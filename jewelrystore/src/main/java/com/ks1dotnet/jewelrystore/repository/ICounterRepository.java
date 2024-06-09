package com.ks1dotnet.jewelrystore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Counter;
import com.ks1dotnet.jewelrystore.entity.Product;

@Repository
public interface ICounterRepository extends JpaRepository<Counter, Integer> {
    @Query(value = "SELECT * FROM counter WHERE status = 1", nativeQuery = true)
    public List<Counter> findAllActiveCounters();

    public List<Counter> findByStatus(boolean status);

    public Optional<Counter> findByName(String name);


    
}
