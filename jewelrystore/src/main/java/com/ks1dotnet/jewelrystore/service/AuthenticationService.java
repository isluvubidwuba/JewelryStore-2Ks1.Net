package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.repository.IAuthenticationRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IAuthenticationService;

@Service
public class AuthenticationService implements IAuthenticationService {
    @Autowired
    private IAuthenticationRepository iAuthenticationRepository;

    @Override
    public List<Employee> findAll() {
        return iAuthenticationRepository.findAll();
    }

    @Override
    public Employee findById(int id) {
        return iAuthenticationRepository.findById(id).orElse(null);
    }

    @Override
    public Employee findByPinCode(String pin_code) {
        // TODO Auto-generated method stub
        return iAuthenticationRepository.findByPinCode(pin_code);
    }

}
