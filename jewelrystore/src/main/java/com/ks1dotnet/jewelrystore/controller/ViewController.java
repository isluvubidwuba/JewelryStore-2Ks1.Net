package com.ks1dotnet.jewelrystore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.ks1dotnet.jewelrystore.Enum.PageTemplate;
import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;

@Controller
public class ViewController {

    @Autowired
    private IEmployeeService iEmployeeService;

    @GetMapping(value = {"/", "/{page}"})
    public String home(@PathVariable(required = false) String page) {
        if (page == null)
            page = "login";
        String template = null;
        try {

            String role =
                    ((EmployeeDTO) iEmployeeService.myProfile().getData()).getRole().getName();
            if (page.equals("login") && role.equals("STAFF"))
                page = "home";
            else if (page.equals("login"))
                page = "dashboard";
            template = PageTemplate.getTemplate(page, role);
            return template;
        } catch (Exception e) {
            if (page.equals("goldPrice") || page.equals("login"))
                template = PageTemplate.getTemplate(page);
            else
                template = PageTemplate.getTemplate("404");
            return template;
        }
    }

}
