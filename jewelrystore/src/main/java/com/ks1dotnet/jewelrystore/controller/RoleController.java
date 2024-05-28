package com.ks1dotnet.jewelrystore.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@RestController
@RequestMapping("/role")
@CrossOrigin("*")
public class RoleController {

    @Autowired
    private IRoleService iRoleService;

    @GetMapping("/list")
    private ResponseEntity<?> findAll() {
        List<RoleDTO> listRoleDTO = iRoleService.findAll();
        ResponseData ResponseData = new ResponseData();
        ResponseData.setStatus(HttpStatus.OK);
        ResponseData.setData(listRoleDTO);
        return new ResponseEntity<>(ResponseData, HttpStatus.OK);
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insertRole(@RequestParam String roleName) {
        boolean isSuccess = iRoleService.insertRole(roleName);
        ResponseData ResponseData = new ResponseData();

        if (isSuccess) {
            ResponseData.setStatus(HttpStatus.OK);
            return new ResponseEntity<>("Role added successfully!", HttpStatus.CREATED);
        } else {
            ResponseData.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>("Error adding role.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
