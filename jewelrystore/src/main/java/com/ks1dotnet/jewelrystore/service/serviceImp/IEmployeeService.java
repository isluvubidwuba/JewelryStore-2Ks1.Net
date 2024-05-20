package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.entity.employee;

public interface IEmployeeService {
    public List<employee> findAll();

    public employee findById(int id);

    public employee findByPinCode(String pin_code);

}
