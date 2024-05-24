package com.ks1dotnet.jewelrystore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.repository.IRoleRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private IRoleRepository iRoleRepository;

    @Override
    public List<Role> findAll() {
        return iRoleRepository.findAll();
    }

    @Override
    public Role findById(int roleId) {
        return iRoleRepository.findById(roleId).orElse(null);
    }

    @Override
    public List<Role> findByIds(List<Integer> roleIds) {
        return iRoleRepository.findAllById(roleIds);
    }

}
