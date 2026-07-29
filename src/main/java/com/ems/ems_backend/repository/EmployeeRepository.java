package com.ems.ems_backend.repository;

import com.ems.ems_backend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByActiveTrue();

    List<Employee> findByActiveFalse();

    long countByActiveTrue();
}