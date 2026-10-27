package com.ems.ems_backend.service;

import com.ems.ems_backend.exception.ForbiddenException;
import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Department;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.model.Role;
import com.ems.ems_backend.model.User;
import com.ems.ems_backend.repository.DepartmentRepository;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.querydsl.QPageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Department engineering;
    private User teamLead;
    private Employee existing;

    @BeforeEach
    void setup() {
        engineering = new Department();
        engineering.setId(1L);

        Employee leadAsEmployee = new Employee();
        leadAsEmployee.setDepartment(engineering);

        teamLead = new User();
        teamLead.setUsername("lead@ems.com");
        teamLead.setRole(Role.TEAM_LEAD);
        teamLead.setEmployee(leadAsEmployee);

        existing = new Employee();
        existing.setId(7L);
        existing.setDepartment(engineering);
        existing.setSalary(75000.0);
        existing.setEmail("ravi@ems.com");
    }

    @Test
    void getEmployeeById_returnsEmployee_whenFound() {
        //Arrange
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Test Kafka");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        //Act
        Employee result = employeeService.getEmployeeById(1L);

        //Assert
        assertEquals("Test Kafka", result.getName());
    }

    @Test
    void getEmployeeById_throwsException_whenNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        //Act and Assert
        assertThrows(ResourceNotFoundException.class, () ->
                employeeService.getEmployeeById(99L));
    }

    @Test
    void updateEmployee_throwsForbidden_whenTeamLeadChangesSalary() {

        Employee updated = requestFor(90000.0);
        mockLookups();

        // Act + Assert
        assertThrows(ForbiddenException.class,
                () -> employeeService.updateEmployee(7L, updated, "lead@ems.com"));
    }

    @Test
    void updateEmployee_allowsUpdate_whenTeamLeadKeepsSameSalary() {

        Employee updated = requestFor(75000.0);
        mockLookups();

        // Act + Assert
        assertDoesNotThrow(() -> employeeService.updateEmployee(7L, updated, "lead@ems.com"));
    }

    @Test
    void updateEmployee_throwsForbidden_whenTeamLeadEditsOtherDepartment() {
        Department sales = new Department();
        sales.setId(2L);
        existing.setDepartment(sales);
        Employee updated = requestFor(75000.0);

        when(employeeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("lead@ems.com")).thenReturn(Optional.of(teamLead));

        // Act + Assert
        assertThrows(ForbiddenException.class,
                () -> employeeService.updateEmployee(7L, updated, "lead@ems.com"));
    }
    
    @Test
    void getActiveEmployees_returnsPageFromRepository() {
        Pageable pageable = PageRequest.of(0,2);

        Employee employee1 = new Employee();
        employee1.setName("Ram");

        Employee employee2 = new Employee();
        employee2.setName("Priya");

        List<Employee> employees = List.of(employee1, employee2);

        Page<Employee> expectedPage =
                new PageImpl<>(employees, pageable, 1948);

        when(employeeRepository.findByActiveTrue(pageable)).thenReturn(expectedPage);

        Page<Employee> result =
                employeeService.getActiveEmployees(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals(1948, result.getTotalElements());
    }

    // builds the request body: same department, same email, given salary
    private Employee requestFor(Double salary) {
        Department requestedDept = new Department();
        requestedDept.setId(1L);
        Employee updated = new Employee();
        updated.setDepartment(requestedDept);
        updated.setEmail("ravi@ems.com");
        updated.setSalary(salary);
        return updated;
    }

    // the three mocks that updateEmployee needs
    private void mockLookups() {
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(userRepository.findByUsername("lead@ems.com")).thenReturn(Optional.of(teamLead));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(engineering));
    }
}