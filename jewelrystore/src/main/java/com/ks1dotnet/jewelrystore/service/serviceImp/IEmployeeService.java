package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.entity.Employee;

public interface IEmployeeService {

    public List<Employee> findAll();
    public Employee save(Employee employee);
    public Employee findById(Integer id);
    
}
