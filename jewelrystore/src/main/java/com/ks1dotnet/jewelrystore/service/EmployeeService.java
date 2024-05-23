package com.ks1dotnet.jewelrystore.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ks1dotnet.jewelrystore.dto.EmployeeDTO;
import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;
import com.ks1dotnet.jewelrystore.service.serviceImp.IEmployeeService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IFileService;
import com.ks1dotnet.jewelrystore.service.serviceImp.IRoleService;

@Service
public class EmployeeService implements IEmployeeService {
   @Autowired
   private IEmployeeRepository iEmployeeRepository;
   @Autowired
   private IFileService iFileService;
   @Autowired
   private IRoleService iRoleService;

   @Override
   public List<Employee> findAll() {
      return iEmployeeRepository.findAll();
   }

   @Override
   public Employee save(Employee employee) {
      return iEmployeeRepository.save(employee);
   }

   @Override
   public Employee findById(Integer id) {
      return iEmployeeRepository.findById(id).orElse(null);
   }

   @Override
   public Map<String, Object> getHomePageEmployee(int page) {
      Map<String, Object> response = new HashMap<>();
      PageRequest pageRequest = PageRequest.of(page, 15);
      List<EmployeeDTO> employeeDTOs = new ArrayList<>();
      Page<Employee> listData = iEmployeeRepository.findAll(pageRequest);

      for (Employee e : listData) {
         employeeDTOs.add(e.getDTO());

      }
      response.put("employees", employeeDTOs);
      response.put("totalPages", listData.getTotalPages());
      response.put("currentPage", page);
      return response;
   }

   @Override
   public boolean insertEmployee(MultipartFile file, String firstName, String lastName, String pinCode,
         String phoneNumber, String email, String address, int roleId , boolean status) {
      boolean isInsertSuccess = false;
      boolean isSaveFileSuccess = iFileService.savefile(file);
      if (isSaveFileSuccess) {
         Employee employee = new Employee();
         employee.setFirstName(firstName);
         employee.setLastName(lastName);
         employee.setPinCode(pinCode);
         employee.setPhoneNumber(phoneNumber);
         employee.setEmail(email);
         employee.setAddress(address);
         employee.setStatus(status);
         employee.setRole(iRoleService.findById(roleId));
         employee.setImage(file.getOriginalFilename());
         iEmployeeRepository.save(employee);
         isInsertSuccess = true;
      }
      return isInsertSuccess;
   }

   @Override
   public EmployeeDTO updateEmployee(MultipartFile file, int id, String firstName, String lastName, String pinCode,
         String phoneNumber, String email, String address, boolean status, int roleId) {
      boolean isSaveFileSuccess = iFileService.savefile(file);
      Optional<Employee> employee = iEmployeeRepository.findById(id);
      System.out.println(employee);
      EmployeeDTO employeeDTO = new EmployeeDTO();
      if (employee.isPresent()) {
         Employee employee1 = new Employee();
         employee1.setId(id);
         employee1.setFirstName(firstName);
         employee1.setLastName(lastName);
         employee1.setPinCode(pinCode);
         employee1.setPhoneNumber(phoneNumber);
         employee1.setEmail(email);
         employee1.setAddress(address);
         employee1.setStatus(status);
         employee1.setRole(iRoleService.findById(roleId));
         if (isSaveFileSuccess) {
            employee1.setImage(file.getOriginalFilename());
         }else{
            employee1.setImage(employee.get().getImage());
         }
         iEmployeeRepository.save(employee1);
         employeeDTO = employee1.getDTO();
      }
      return employeeDTO;
   }

   @Override
   public Employee listEmployee(int id) {
      return iEmployeeRepository.findById(id).orElse(null);
   }

}
