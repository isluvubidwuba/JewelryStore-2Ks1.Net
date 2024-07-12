package com.ks1dotnet.jewelrystore.service.serviceImp;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.payload.ResponseData;

public interface IEmployeeService {

        public List<Employee> findAll();

        public Employee save(Employee employee);

        public Employee findById(String id);

        // List<Employee> getHomePageEmployee(int page);
        public ResponseData getHomePageEmployee(int page, String id);

        public ResponseData insertEmployee(MultipartFile file, String firstName, String lastName,
                        String phoneNumber, String email, String address, int roleId);

        public ResponseData updateEmployee(MultipartFile file, String id, String firstName,
                        String lastName, int roleId, String pinCode, boolean status,
                        String phoneNumber, String email, String address);

        public ResponseData listEmployee(String id);

        public ResponseData findByCriteria(String criteria, String query, int page);

        public ResponseData getStaff();

        public ResponseData deleteEmployee(String id);


        public ResponseData myProfile();
}
