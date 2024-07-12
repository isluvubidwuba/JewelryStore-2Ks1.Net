package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;

import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.entity.Role;

public interface IRoleService {
    public List<RoleDTO> findAll(String id);

    public Role findById(int roleId);

    public Role save(Role role);

    public boolean insertRole(String roleName);
}
