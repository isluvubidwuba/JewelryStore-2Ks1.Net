package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.exception.ApplicationException;
import com.ks1dotnet.jewelrystore.repository.IRoleRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;
import io.jsonwebtoken.Claims;

@Service
public class RoleService implements IRoleService {
    @Autowired
    private IRoleRepository iRoleRepository;

    @Override
    public List<RoleDTO> findAll() {


        Claims RTTokenClaims = JwtUtilsHelper.getClaims("at");
        List<Role> listRole;
        if ("admin".equals(RTTokenClaims.getSubject()))
            listRole = iRoleRepository.findAll();
        else
            listRole = iRoleRepository.findAllExceptRole(1);
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
