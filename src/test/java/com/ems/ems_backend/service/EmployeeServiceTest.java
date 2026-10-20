package com.ems.ems_backend.service;

import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

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
}