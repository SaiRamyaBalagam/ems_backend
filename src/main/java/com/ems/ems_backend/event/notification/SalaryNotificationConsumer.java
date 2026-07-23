package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.SalaryPaid;
import com.ems.ems_backend.event.SalaryDeleted;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@KafkaListener(topics = "salary-events", groupId = "notification-service")
@Slf4j
public class SalaryNotificationConsumer {

    @KafkaHandler
    public void onSalaryPaid(SalaryPaid event) {
        log.info("[notification-service] Payslip ready for employeeId={}: {} for {}/{}",
                event.employeeId(), event.netSalary(), event.payMonth(), event.payYear());
    }

    @KafkaHandler
    public void onSalarDeleted(SalaryDeleted event) {
        log.info("[notification-service] Salary credit deleted for employeeId={}: {} for {}/{}",
                event.employeeId(), event.netSalary(), event.payMonth(), event.payYear());
    }
}
