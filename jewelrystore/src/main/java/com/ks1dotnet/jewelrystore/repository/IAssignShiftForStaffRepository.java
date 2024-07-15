package com.ks1dotnet.jewelrystore.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;
import com.ks1dotnet.jewelrystore.entity.AssignShiftForStaff;
import com.ks1dotnet.jewelrystore.entity.Employee;

@Repository
public interface IAssignShiftForStaffRepository
                extends JpaRepository<AssignShiftForStaff, Integer> {

        @Query("SELECT acs FROM AssignCountersForStaff acs " +
                        "JOIN FETCH acs.assignShiftForStaff ass " +
                        "JOIN FETCH acs.counter cs " +
                        "JOIN FETCH ass.employee e " +
                        "WHERE ass.date BETWEEN :startDate AND :endDate")
        List<AssignCountersForStaff> findAllByDateBetween(@Param("startDate") Date startDate,
                        @Param("endDate") Date endDate);

        Optional<AssignShiftForStaff> findByDateAndEmployee(Date date, Employee employee);

        @Query("SELECT acs FROM AssignCountersForStaff acs " +
                        "JOIN FETCH acs.assignShiftForStaff ass " +
                        "JOIN FETCH acs.counter cs " +
                        "JOIN FETCH ass.employee e " +
                        "WHERE ass.date BETWEEN :startDate AND :endDate " +
                        "AND e.id = :employeeId")
        List<AssignCountersForStaff> findAllByDateBetweenAndEmployeeId(
                        @Param("startDate") Date startDate,
                        @Param("endDate") Date endDate,
                        @Param("employeeId") String employeeId);
}
