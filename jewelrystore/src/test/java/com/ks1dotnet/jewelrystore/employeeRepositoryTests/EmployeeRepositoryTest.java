package com.ks1dotnet.jewelrystore.employeeRepositoryTests;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.ks1dotnet.jewelrystore.entity.Employee;
import com.ks1dotnet.jewelrystore.entity.Role;
import com.ks1dotnet.jewelrystore.repository.IEmployeeRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class EmployeeRepositoryTest {
    @Autowired
    private IEmployeeRepository iEmployeeRepository;

    private Employee employee;

    @BeforeEach
    public void setUp() {
        Role role = new Role(1, "ADMIN", null, null);
        employee = Employee.builder()
                .firstName("tan")
                .lastName("Nguyen")
                .pinCode("123")
                .phoneNumber("9123912421")
                .email("hihihi@example.com")
                .address("hihi")
                .role(role)
                .status(true)
                .build();
    }

    @Test
    public void whenSave_thenReturnSavedEmployee() {
        // Act
        Employee savedEmployee = iEmployeeRepository.save(employee);

        // Assert
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId());
        assertThat(savedEmployee.getFirstName()).isEqualTo("tan");
    }

    @Test
    public void whenFindById_thenReturnEmployee() {
        System.out.println("Starting test: whenFindById_thenReturnEmployee");

        // Arrange
        Employee savedEmployee = iEmployeeRepository.save(employee);
        System.out.println("Employee saved: " + savedEmployee);

        // Act
        Employee foundEmployee = iEmployeeRepository.findById(savedEmployee.getId()).orElse(null);
        System.out.println("Employee found: " + foundEmployee);

        // Assert
        assertThat(foundEmployee).isNotNull();
        assertThat(foundEmployee.getId()).isEqualTo(savedEmployee.getId());
    }

    @Test
    public void whenUpdate_thenReturnUpdatedEmployee() {
        // Arrange
        Employee savedEmployee = iEmployeeRepository.save(employee);
        savedEmployee.setFirstName("updatedFirstName");

        // Act
        Employee updatedEmployee = iEmployeeRepository.save(savedEmployee);

        // Assert
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.getFirstName()).isEqualTo("updatedFirstName");
    }

    @Test
    public void whenDelete_thenEmployeeIsInactive() {
        // Arrange
        Employee savedEmployee = iEmployeeRepository.save(employee);

        // Act
        savedEmployee.setStatus(false);
        Employee updatedEmployee = iEmployeeRepository.save(savedEmployee);

        // Assert
        assertThat(updatedEmployee).isNotNull();
        assertThat(updatedEmployee.isStatus()).isFalse();
    }

}
