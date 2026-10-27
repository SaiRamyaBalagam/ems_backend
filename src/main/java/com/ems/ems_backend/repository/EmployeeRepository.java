package com.ems.ems_backend.repository;

import com.ems.ems_backend.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByActiveTrue();

    @EntityGraph(attributePaths = "department")
    Page<Employee> findByActiveTrue(Pageable pageable);

    List<Employee> findByActiveFalse();

    long countByActiveTrue();
}