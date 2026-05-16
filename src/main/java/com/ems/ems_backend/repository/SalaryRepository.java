package com.ems.ems_backend.repository;

import com.ems.ems_backend.model.Salary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndPayMonthAndPayYear(Long employeeId, int payMonth, int payYear);
    List<Salary> findByPayMonthAndPayYear(int payMonth, int payYear);
}
