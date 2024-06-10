package com.ks1dotnet.jewelrystore.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    IEmployeeRepository iEmployeeRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Employee employee = iEmployeeRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Id user not found"));
        if (employee == null) {
            throw new UsernameNotFoundException("Username not found: ");
        }

        return new User(username, employee.getPinCode(), new ArrayList<>());
    }

}
