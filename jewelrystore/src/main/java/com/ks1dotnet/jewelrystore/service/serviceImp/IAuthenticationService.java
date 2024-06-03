package com.ks1dotnet.jewelrystore.service.serviceImp;

import com.ks1dotnet.jewelrystore.entity.Employee;

public interface IAuthenticationService {
    public Employee findById(String id);
}
