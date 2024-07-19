package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.ks1dotnet.jewelrystore.Enum.PageTemplate;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.utils.JwtUtilsHelper;

@Controller
public class ViewController {

    @Autowired
    private IEmployeeService iEmployeeService;

    @GetMapping(value = {"/", "/{page}"})
    public String home(@PathVariable(required = false) String page) {
        if (page == null)
            page = "login";
        try {

            String role =
                    ((EmployeeDTO) iEmployeeService.myProfile().getData()).getRole().getName();
            System.out.println(role);
            String template = PageTemplate.getTemplate(page, role);
            return template;
        } catch (Exception e) {
            String template = PageTemplate.getTemplate(page);
            return template;
        }
    }

}
