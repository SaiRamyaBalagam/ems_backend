package com.ems.ems_backend.service;

import com.ems.ems_backend.dto.SalaryResponse;
import com.ems.ems_backend.event.SalaryPaid;
import com.ems.ems_backend.exception.DuplicateResourceException;
import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.model.Salary;
import com.ems.ems_backend.model.SalaryStatus;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public SalaryResponse addSalary(Long employeeId, BigDecimal baseSalary, BigDecimal bonus,
                                    BigDecimal deductions, int payMonth, int payYear) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (payMonth < 1 || payMonth > 12) {
            throw new IllegalArgumentException("Pay month must be between 1 and 12");
        }

        if (salaryRepository.existsByEmployeeIdAndPayMonthAndPayYear(employeeId, payMonth, payYear)) {
            throw new DuplicateResourceException(
                    "Salary record already exists for employee " + employeeId + " for " + payMonth + "/" + payYear);
        }

        BigDecimal effectiveBonus = bonus != null ? bonus : BigDecimal.ZERO;
        BigDecimal effectiveDeductions = deductions != null ? deductions : BigDecimal.ZERO;
        BigDecimal netSalary = baseSalary.add(effectiveBonus).subtract(effectiveDeductions);

        Salary salary = new Salary();
        salary.setEmployee(employee);
        salary.setBaseSalary(baseSalary);
        salary.setBonus(effectiveBonus);
        salary.setDeductions(effectiveDeductions);
        salary.setNetSalary(netSalary);
        salary.setPayMonth(payMonth);
        salary.setPayYear(payYear);
        salary.setStatus(SalaryStatus.PENDING);

        return new SalaryResponse(salaryRepository.save(salary));
    }

    public List<SalaryResponse> getAllSalaries() {
        return salaryRepository.findAll().stream()
                .map(SalaryResponse::new)
                .toList();
    }

    public List<SalaryResponse> getSalariesByMonthAndYear(int payMonth, int payYear) {
        if (payMonth < 1 || payMonth > 12) {
            throw new IllegalArgumentException("Pay month must be between 1 and 12");
        }
        return salaryRepository.findByPayMonthAndPayYear(payMonth, payYear).stream()
                .map(SalaryResponse::new)
                .toList();
    }

    public List<SalaryResponse> getSalariesByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return salaryRepository.findByEmployeeId(employeeId).stream()
                .map(SalaryResponse::new)
                .toList();
    }

    public SalaryResponse updateSalary(Long id, BigDecimal baseSalary, BigDecimal bonus, BigDecimal deductions) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + id));

        BigDecimal effectiveBase = baseSalary != null ? baseSalary : salary.getBaseSalary();
        BigDecimal effectiveBonus = bonus != null ? bonus : salary.getBonus();
        BigDecimal effectiveDeductions = deductions != null ? deductions : salary.getDeductions();

        salary.setBaseSalary(effectiveBase);
        salary.setBonus(effectiveBonus);
        salary.setDeductions(effectiveDeductions);
        salary.setNetSalary(effectiveBase.add(effectiveBonus).subtract(effectiveDeductions));

        return new SalaryResponse(salaryRepository.save(salary));
    }

    @Transactional
    public SalaryResponse updateSalaryStatus(Long id, String status) {
        Salary salary = salaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with id: " + id));

        SalaryStatus newStatus = SalaryStatus.valueOf(status.toUpperCase());
        salary.setStatus(newStatus);
        Salary saved = salaryRepository.save(salary);

        if (newStatus == SalaryStatus.PAID) {
            eventPublisher.publishEvent(new SalaryPaid(
                    saved.getId(),
                    saved.getEmployee().getId(),
                    saved.getNetSalary(),
                    saved.getPayMonth(),
                    saved.getPayYear(),
                    Instant.now()
            ));
        }

        return new SalaryResponse(saved);
    }

    public void deleteSalary(Long id) {
        if (!salaryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salary record not found with id: " + id);
        }
        salaryRepository.deleteById(id);
    }
}
