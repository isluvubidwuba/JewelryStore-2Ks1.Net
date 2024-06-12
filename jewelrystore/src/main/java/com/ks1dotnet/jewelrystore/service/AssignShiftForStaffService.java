package com.ks1dotnet.jewelrystore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.repository.IAssignShiftForStaffRepository;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAssignShiftForStaffService;

@Service
public class AssignShiftForStaffService implements IAssignShiftForStaffService {

    @Autowired
    private IAssignShiftForStaffRepository assignShiftForStaffRepository;

    @Autowired
    private IEmployeeRepository iEmployeeRepository;

}
