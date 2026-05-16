package com.ems.ems_backend.controller;

import com.ems.ems_backend.dto.SalaryRequest;
import com.ems.ems_backend.dto.SalaryResponse;
import com.ems.ems_backend.dto.SalaryStatusRequest;
import com.ems.ems_backend.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;

    @PostMapping
    public ResponseEntity<SalaryResponse> addSalary(@RequestBody SalaryRequest request) {
        SalaryResponse salary = salaryService.addSalary(
                request.getEmployeeId(),
                request.getBaseSalary(),
                request.getBonus(),
                request.getDeductions(),
                request.getPayMonth(),
                request.getPayYear()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(salary);
    }

    @GetMapping
    public ResponseEntity<List<SalaryResponse>> getAllSalaries() {
        return ResponseEntity.ok(salaryService.getAllSalaries());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<SalaryResponse>> getSalariesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(salaryService.getSalariesByEmployee(employeeId));
    }

    @GetMapping("/month/{month}/{year}")
    public ResponseEntity<List<SalaryResponse>> getSalariesByMonth(
            @PathVariable int month,
            @PathVariable int year) {
        return ResponseEntity.ok(salaryService.getSalariesByMonthAndYear(month, year));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaryResponse> updateSalary(@PathVariable Long id,
                                                       @RequestBody SalaryRequest request) {
        return ResponseEntity.ok(salaryService.updateSalary(
                id,
                request.getBaseSalary(),
                request.getBonus(),
                request.getDeductions()
        ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SalaryResponse> updateSalaryStatus(@PathVariable Long id,
                                                             @RequestBody SalaryStatusRequest request) {
        return ResponseEntity.ok(salaryService.updateSalaryStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSalary(@PathVariable Long id) {
        salaryService.deleteSalary(id);
        return ResponseEntity.ok("Salary record deleted successfully");
    }
}
