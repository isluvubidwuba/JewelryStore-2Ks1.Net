package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Employee;

@Repository
public interface IAuthenticationRepository extends JpaRepository<Employee, String> {
    public Employee findByPinCode(String pin_code);
}
