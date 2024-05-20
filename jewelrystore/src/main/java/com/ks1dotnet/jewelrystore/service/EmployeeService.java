package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.employee;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@Service
public class EmployeeService implements IEmployeeService {
    @Autowired
    private IEmployeeRepository iEmployeeRepository;

    @Override
    public List<employee> findAll() {
        return iEmployeeRepository.findAll();
    }

    @Override
    public employee findById(int id) {
        return iEmployeeRepository.findById(id).orElse(null);
    }

    @Override
    public employee findByPinCode(String pin_code) {
        // TODO Auto-generated method stub
        return iEmployeeRepository.findByPinCode(pin_code);
    }

}
