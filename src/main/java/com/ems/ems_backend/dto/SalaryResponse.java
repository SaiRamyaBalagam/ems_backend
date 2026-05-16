package com.ems.ems_backend.dto;

import com.ems.ems_backend.model.Salary;
import com.ems.ems_backend.model.SalaryStatus;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SalaryResponse {
    private final Long id;
    private final Long employeeId;
    private final String employeeName;
    private final BigDecimal baseSalary;
    private final BigDecimal bonus;
    private final BigDecimal deductions;
    private final BigDecimal netSalary;
    private final int payMonth;
    private final int payYear;
    private final SalaryStatus status;

    public SalaryResponse(Salary salary) {
        this.id = salary.getId();
        this.employeeId = salary.getEmployee().getId();
        this.employeeName = salary.getEmployee().getName();
        this.baseSalary = salary.getBaseSalary();
        this.bonus = salary.getBonus();
        this.deductions = salary.getDeductions();
        this.netSalary = salary.getNetSalary();
        this.payMonth = salary.getPayMonth();
        this.payYear = salary.getPayYear();
        this.status = salary.getStatus();
    }
}
