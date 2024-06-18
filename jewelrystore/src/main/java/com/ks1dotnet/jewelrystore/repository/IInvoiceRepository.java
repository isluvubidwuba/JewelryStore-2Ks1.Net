package com.ks1dotnet.jewelrystore.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.Invoice;

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, Integer> {
    Page<Invoice> findAll(Pageable pageable);

    List<Invoice> findByEmployeeId(String employeeId);

    List<Invoice> findByDateBetween(Date startDate, Date endDate);

    List<Invoice> findByUserInfoId(int userId);

    List<Invoice> findByEmployeeIdAndDateBetween(String employeeId, Date startDate, Date endDate);

    @Query("SELECT i FROM Invoice i WHERE YEAR(i.date) = :year AND MONTH(i.date) = :month")
    List<Invoice> findByMonthAndYear(@Param("year") int year, @Param("month") int month);

    @Query("SELECT i FROM Invoice i WHERE YEAR(i.date) = :year AND QUARTER(i.date) = :quarter")
    List<Invoice> findByQuarterAndYear(@Param("year") int year, @Param("quarter") int quarter);

    @Query("SELECT i FROM Invoice i WHERE YEAR(i.date) = :year")
    List<Invoice> findByYear(@Param("year") int year);

    @Query("SELECT i FROM Invoice i WHERE i.employee.id = :employeeId AND YEAR(i.date) = :year AND MONTH(i.date) = :month")
    List<Invoice> findByEmployeeIdAndMonthAndYear(@Param("employeeId") String employeeId, @Param("year") int year,
            @Param("month") int month);

    @Query("SELECT i FROM Invoice i WHERE i.employee.id = :employeeId AND YEAR(i.date) = :year")
    List<Invoice> findByEmployeeIdAndYear(@Param("employeeId") String employeeId, @Param("year") int year);
    // Quý 1 (Q1) bao gồm tháng 1, 2 và 3.
    // Quý 2 (Q2) bao gồm tháng 4, 5 và 6.
    // Quý 3 (Q3) bao gồm tháng 7, 8 và 9.
    // Quý 4 (Q4) bao gồm tháng 10, 11 và 12.
}
