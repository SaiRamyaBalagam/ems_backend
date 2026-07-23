package com.ems.ems_backend.repository;

import com.ems.ems_backend.model.Salary;
import com.ems.ems_backend.model.SalaryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface SalaryRepository extends JpaRepository<Salary, Long> {
    List<Salary> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeIdAndPayMonthAndPayYear(Long employeeId, int payMonth, int payYear);
    List<Salary> findByPayMonthAndPayYear(int payMonth, int payYear);

    @Query("SELECT COALESCE(SUM(s.netSalary), 0) FROM Salary s " +
            "WHERE s.status = :status AND s.payMonth = :payMonth AND s.payYear = :payYear")
    BigDecimal sumNetSalaryByStatusAndPayMonthAndPayYear(
            @Param("status") SalaryStatus status, @Param("payMonth") int payMonth, @Param("payYear") int payYear);
}
