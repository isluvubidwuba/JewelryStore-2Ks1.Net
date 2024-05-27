package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.repository.IRoleRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private IRoleRepository iRoleRepository;

    @Override
    public List<RoleDTO> findAll() {
        List<Role> listRole = iRoleRepository.findAll();
        List<RoleDTO> listRoleDTO = new ArrayList<>();

        for (Role role : listRole) {
            listRoleDTO.add(role.getDTO());
        }

        return listRoleDTO;
    }

    @Override
    public Role findById(int roleId) {
        return iRoleRepository.findById(roleId).orElse(null);
    }

    @Override
    public Role save(Role role) {
        return iRoleRepository.save(role);
    }

    @Override
    public boolean insertRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return save(role) != null;
    }

}
