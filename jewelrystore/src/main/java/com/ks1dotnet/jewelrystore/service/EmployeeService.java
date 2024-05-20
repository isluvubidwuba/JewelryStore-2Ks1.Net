package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@Service
public class EmployeeService implements IEmployeeService {
    @Autowired
    private IEmployeeRepository iEmployeeRepository;

    @Override
    public List<Employee> findAll() {
        return iEmployeeRepository.findAll();
    }

    @Override
    public Employee findById(int id) {
        return iEmployeeRepository.findById(id).orElse(null);
    }

    @Override
    public Employee findByPinCode(String pin_code) {
        // TODO Auto-generated method stub
        return iEmployeeRepository.findByPinCode(pin_code);
    }

}
