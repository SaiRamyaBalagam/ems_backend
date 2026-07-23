package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.SalaryPaid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalaryNotificationConsumer {

    @KafkaListener(topics = "salary-events", groupId = "notification-service")
    public void onSalaryPaid(SalaryPaid event) {
        log.info("[notification-service] Payslip ready for employeeId={}: {} for {}/{}",
                event.employeeId(), event.netSalary(), event.payMonth(), event.payYear());
    }
}
