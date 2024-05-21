package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public Employee save(Employee employee) {
       return iEmployeeRepository.save(employee);
    }

   @Override
   public Employee findById(Integer id) {
      return iEmployeeRepository.findById(id).orElse(null);
   }

   @Override
   public Page<Employee> getPaginatedEntities(int page, int size) {
      Pageable pageable = PageRequest.of(page, size);
      return iEmployeeRepository.findAll(pageable);
   }

   // @Override
   // public List<Employee> getHomePageEmployee(int page) {
   //    List<Employee> listEmployees = new ArrayList<>();
   //    PageRequest pageRequest = PageRequest.of(page, 7);
   //    Page<Employee> listData = iEmployeeRepository.findAll(pageRequest);
   //    for(Employee e : listData){
   //       listEmployees.add(e);
   //    }
   //    return listEmployees;
   // }

  

   

}
