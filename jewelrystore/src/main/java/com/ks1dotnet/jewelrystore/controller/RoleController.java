package com.ks1dotnet.jewelrystore.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.payload.responseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@RestController
@RequestMapping("/role")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private IRoleService iRoleService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        responseData responseData = new responseData();
        List<Integer> roleIds = Arrays.asList(1, 2, 3);
        List<Role> listRole = iRoleService.findByIds(roleIds);
        responseData.setData(listRole);
        return new ResponseEntity<>(responseData, HttpStatus.OK);
    }
}
