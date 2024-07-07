package com.ks1dotnet.jewelrystore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ks1dotnet.jewelrystore.entity.AssignCountersForStaff;

@Repository
public interface IAssignCountersForStaffRepository
        extends JpaRepository<AssignCountersForStaff, Integer> {

}
