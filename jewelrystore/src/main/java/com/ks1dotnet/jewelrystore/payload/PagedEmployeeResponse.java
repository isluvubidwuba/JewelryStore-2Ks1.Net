package com.ks1dotnet.jewelrystore.payload;

import java.util.List;

import org.springframework.data.domain.Page;

import com.ks1dotnet.jewelrystore.entity.Employee;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PagedEmployeeResponse {
    private List<Employee> allEmployees;
    private Page<Employee> paginatedEmployees;
    private int currentPage;
    private int totalPages;
    private long totalItems;
    

    public PagedEmployeeResponse(List<Employee> allEmployees, Page<Employee> paginatedEmployees) {
        this.allEmployees = allEmployees;
        this.paginatedEmployees = paginatedEmployees;
    }

    // Getters và setters
    public List<Employee> getAllEmployees() {
        return allEmployees;
    }

    public void setAllEmployees(List<Employee> allEmployees) {
        this.allEmployees = allEmployees;
    }

    public Page<Employee> getPaginatedEmployees() {
        return paginatedEmployees;
    }

    public void setPaginatedEmployees(Page<Employee> paginatedEmployees) {
        this.paginatedEmployees = paginatedEmployees;
    }
}

