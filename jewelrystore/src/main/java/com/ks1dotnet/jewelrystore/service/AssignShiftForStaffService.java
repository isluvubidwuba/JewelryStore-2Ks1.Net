package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.AssignShiftForStaffDTO;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.AssignShiftForStaff;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
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
