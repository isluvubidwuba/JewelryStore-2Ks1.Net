package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Employee;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, String> {

    public Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    public Page<Employee> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    public Page<Employee> findByRoleNameContainingIgnoreCase(String roleName, Pageable pageable);

    public Page<Employee> findByStatus(Boolean status, Pageable pageable);

    public Page<Employee> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(String lastName, String firstName,
            Pageable pageable);
}
