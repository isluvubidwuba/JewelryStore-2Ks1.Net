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
    public Employee findById(String id) {
        return iAuthenticationRepository.findById(id).orElse(null);
    }

}
