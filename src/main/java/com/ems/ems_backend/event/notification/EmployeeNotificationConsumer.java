package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Stands in for a separate notification microservice. It only knows about
 * the employee-events topic, not the code that produces it — the producer
 * (EmployeeService) has no reference to this class.
 */
@Component
@Slf4j
public class EmployeeNotificationConsumer {

    @KafkaListener(topics = "employee-events", groupId = "notification-service")
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[notification-service] Welcome email queued for {} <{}> (employeeId={}, dept={})",
                event.name(), event.email(), event.employeeId(), event.departmentName());
    }
}
