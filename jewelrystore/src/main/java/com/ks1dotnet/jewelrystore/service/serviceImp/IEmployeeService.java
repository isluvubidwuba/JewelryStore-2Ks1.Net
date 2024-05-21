package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ks1dotnet.jewelrystore.entity.Employee;

public interface IEmployeeService {

    public List<Employee> findAll();
    public Employee save(Employee employee);
    public Employee findById(Integer id);
    // List<Employee> getHomePageEmployee(int page);
    public Page<Employee> getPaginatedEntities(int page, int size);
}
