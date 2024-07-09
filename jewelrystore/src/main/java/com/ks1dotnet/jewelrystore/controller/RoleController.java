package com.ks1dotnet.jewelrystore.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ks1dotnet.jewelrystore.dto.RoleDTO;
import com.ks1dotnet.jewelrystore.payload.ResponseData;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@RestController
@RequestMapping("${apiURL}/role")
@CrossOrigin(origins = "${domain}", allowCredentials = "true")
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

}
