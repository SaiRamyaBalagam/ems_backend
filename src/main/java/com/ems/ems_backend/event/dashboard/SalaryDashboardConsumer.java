package com.ems.ems_backend.event.dashboard;

import com.ems.ems_backend.event.SalaryPaid;
import com.ems.ems_backend.model.SalaryStatus;
import com.ems.ems_backend.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class SalaryDashboardConsumer {

    private final SalaryRepository salaryRepository;

    @KafkaListener(topics = "salary-events", groupId = "dashboard-service")
    public void onSalaryPaid(SalaryPaid event) {
        BigDecimal payrollTotal = salaryRepository.sumNetSalaryByStatusAndPayMonthAndPayYear(
                SalaryStatus.PAID, event.payMonth(), event.payYear());
        log.info("[dashboard-service] Payroll total for {}/{} now {} (triggered by employeeId={})",
                event.payMonth(), event.payYear(), payrollTotal, event.employeeId());
    }
}
