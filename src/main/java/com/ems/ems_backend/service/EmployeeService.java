package com.ems.ems_backend.service;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.event.EmployeeDeletedEvent;
import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Department;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.model.Role;
import com.ems.ems_backend.model.User;
import com.ems.ems_backend.repository.DepartmentRepository;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Employee addEmployee(Employee employee) {
        resolveFullDepartment(employee);
        Employee saved = employeeRepository.save(employee);

        // Auto-create a login account for the new employee
        User user = new User();
        user.setUsername(saved.getEmail());
        user.setPassword(passwordEncoder.encode("Welcome@123"));
        user.setRole(Role.EMPLOYEE);
        user.setEmployee(saved);
        userRepository.save(user);

        Employee full = employeeRepository.findById(saved.getId()).orElse(saved);

        eventPublisher.publishEvent(new EmployeeCreatedEvent(
                full.getId(),
                full.getName(),
                full.getEmail(),
                full.getDepartment() != null ? full.getDepartment().getId() : null,
                full.getDepartment() != null ? full.getDepartment().getName() : null,
                Instant.now()
        ));

        return full;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findByActiveTrue();
    }

    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        Employee existing = getEmployeeById(id);
        existing.setName(updatedEmployee.getName());
        existing.setPosition(updatedEmployee.getPosition());
        existing.setSalary(updatedEmployee.getSalary());

        // If email changed, update the linked user's username too
        if (!existing.getEmail().equals(updatedEmployee.getEmail())) {
            userRepository.findByUsername(existing.getEmail()).ifPresent(user -> {
                user.setUsername(updatedEmployee.getEmail());
                userRepository.save(user);
            });
            existing.setEmail(updatedEmployee.getEmail());
        }

        resolveFullDepartment(updatedEmployee);
        existing.setDepartment(updatedEmployee.getDepartment());

        return employeeRepository.save(existing);
    }

    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = getEmployeeById(id);

        employee.setActive(false);
        employeeRepository.save(employee);

        // Remove linked user account first to avoid FK constraint
        userRepository.findByUsername(employee.getEmail())
                .ifPresent(user -> {
                    user.setEnabled(false);
                    userRepository.save(user);
                        });

        eventPublisher.publishEvent(new EmployeeDeletedEvent(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getDepartment() != null ? employee.getDepartment().getId() : null,
                employee.getDepartment() != null ? employee.getDepartment().getName() : null,
                Instant.now()
        ));
    }

    private void resolveFullDepartment(Employee employee) {
        if (employee.getDepartment() != null && employee.getDepartment().getId() != null) {
            Long deptId = employee.getDepartment().getId();
            Department fullDept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + deptId));
            employee.setDepartment(fullDept);
        }
    }
}
