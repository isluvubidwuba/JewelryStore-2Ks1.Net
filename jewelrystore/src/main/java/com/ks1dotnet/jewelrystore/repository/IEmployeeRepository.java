package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.employee;

@Repository
public interface IEmployeeRepository extends JpaRepository<employee, Integer> {
    public employee findByPinCode(String pin_code);

}
