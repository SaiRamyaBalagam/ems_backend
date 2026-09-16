package com.ems.ems_backend.event.notification;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.event.EmployeeDeletedEvent;
import com.ems.ems_backend.event.EmployeeReactivatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Stands in for a separate notification microservice. It only knows about
 * the employee-events topic, not the code that produces it — the producer
 * (EmployeeService) has no reference to this class.
 *
 * employee-events now carries two event types, so this uses class-level
 * @KafkaListener + @KafkaHandler dispatch (same pattern as
 * LeaveNotificationConsumer) instead of a single-type method listener.
 */
@Component
@KafkaListener(topics = "employee-events", groupId = "notification-service")
@Slf4j
public class EmployeeNotificationConsumer {

    @KafkaHandler
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        log.info("[notification-service] Welcome email queued for {} <{}> (employeeId={}, dept={})",
                event.name(), event.email(), event.employeeId(), event.departmentName());
    }

    @KafkaHandler
    public void onEmployeeDeleted(EmployeeDeletedEvent event) {
        log.info("[notification-service] Offboarding notice for {} <{}> (employeeId={}, dept={})",
                event.name(), event.email(), event.employeeId(), event.departmentName());
    }

    @KafkaHandler
    public void onEmployeeReactivated(EmployeeReactivatedEvent event) {
        log.info("[notification-service] Welcome-back notice for {} <{}> (employeeId={}, dept={})",
                event.name(), event.email(), event.employeeId(), event.departmentName());
    }
}
