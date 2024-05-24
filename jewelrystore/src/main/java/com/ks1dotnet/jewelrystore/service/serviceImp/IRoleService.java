package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.entity.Role;

public interface IRoleService {
    public List<Role> findAll();

    public Role findById(int roleId);

    public List<Role> findByIds(List<Integer> roleIds);
}
