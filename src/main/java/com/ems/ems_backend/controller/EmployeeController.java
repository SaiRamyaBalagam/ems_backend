package com.ems.ems_backend.controller;

import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> addEmployee(@RequestBody Employee employee, Authentication authentication) {
        return new ResponseEntity<>(employeeService.addEmployee(employee, authentication.getName()), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Employee>> getActiveEmployeesPaged(
                @PageableDefault(size = 20, sort = {"name", "id"}) Pageable pageable) {
        return ResponseEntity.ok(employeeService.getActiveEmployees(pageable));
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<Employee>> getInactiveEmployees() {
        return ResponseEntity.ok(employeeService.getInactiveEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee employee,
                                                   Authentication authentication) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Employee deleted successfully");
    }

    @PutMapping("/{id}/reactivate")
    public ResponseEntity<String> reactivateEmployee(@PathVariable Long id, Authentication authentication) {
        employeeService.reactivateEmployee(id, authentication.getName());
        return ResponseEntity.ok("Employee reactivated Successfully");
    }
}
