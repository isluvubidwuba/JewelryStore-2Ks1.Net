package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;

public interface IEmployeeService {

        public List<Employee> findAll();

        public Employee save(Employee employee);

        public Employee findById(String id);

        // List<Employee> getHomePageEmployee(int page);
        public Map<String, Object> getHomePageEmployee(int page);

        public boolean insertEmployee(MultipartFile file, String firstName, String lastName, String pinCode,
                        String phoneNumber, String email, String address, int roleId, boolean status);

        public EmployeeDTO updateEmployee(MultipartFile file, String id, String firstName, String lastName, int roleId,
                        String pinCode, boolean status, String phoneNumber, String email, String address);

        public Employee listEmployee(String id);

        public Map<String, Object> findByCriteria(String criteria, String query, int page);

        // private int id;
        // private String firstName;
        // private String lastName;
        // private String pinCode;
        // private boolean status;
        // private String phoneNumber;
        // private String email;
        // private String address;
        // private RoleDTO role;
        // }
}
