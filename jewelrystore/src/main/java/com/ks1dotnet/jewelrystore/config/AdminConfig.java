package com.ks1dotnet.jewelrystore.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.repository.IRoleRepository;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
public class AdminConfig {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(IEmployeeRepository emp, IRoleRepository role) {
        return args -> {
            if (emp.findAdmin().isEmpty()) {
                Employee admin = new Employee();
                admin.setId("admin");
                admin.setPinCode(passwordEncoder.encode("admin"));
                admin.setRole(role.findById(1).get());
                admin.setStatus(true);
                emp.save(admin);
            }
        };
    }
}
