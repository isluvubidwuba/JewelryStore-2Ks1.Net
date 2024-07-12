package com.ks1dotnet.jewelrystore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Employee;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, String> {

    public Page<Employee> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

    public Page<Employee> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    public Page<Employee> findByRoleNameContainingIgnoreCase(String roleName, Pageable pageable);

    public Page<Employee> findByStatus(Boolean status, Pageable pageable);

    public Page<Employee> findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
            String lastName, String firstName, Pageable pageable);

    public boolean existsByEmail(String email);

    public boolean existsByPhoneNumber(String phoneNumber);

    // Kiểm tra email tồn tại trừ ID hiện tại
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.id != :id AND e.email = :email")
    public boolean existsByEmailExceptId(@Param("email") String email, @Param("id") String id);

    // Kiểm tra số điện thoại tồn tại trừ ID hiện tại
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.id != :id AND e.phoneNumber = :phone")
    public boolean existsByPhoneNumberExceptId(@Param("phone") String phoneNumber, @Param("id") String id);

    public boolean existsById(String id);

    @Query("SELECT e FROM Employee e WHERE e.role.id = 3")
    public List<Employee> findAllStaff();

    @Query("SELECT e FROM Employee e WHERE e.role.id = 1")
    public List<Employee> findAdmin();

    @Query("SELECT u FROM Employee u WHERE u.role.id != :excludedRole")
    Page<Employee> findAllExceptRole(@Param("excludedRole") int excludedRole, Pageable pageable);

}
