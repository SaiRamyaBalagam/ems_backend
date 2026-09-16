package com.ems.ems_backend.event.dashboard;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.event.EmployeeDeletedEvent;
import com.ems.ems_backend.event.EmployeeReactivatedEvent;
import com.ems.ems_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Stands in for a separate dashboard/read-model microservice. Its own
 * consumer group, independent of notification-service and audit-service.
 *
 * employee-events now carries two event types, so this uses class-level
 * @KafkaListener + @KafkaHandler dispatch instead of a single-type method
 * listener.
 */
@Component
@KafkaListener(topics = "employee-events", groupId = "dashboard-service")
@RequiredArgsConstructor
@Slf4j
public class EmployeeDashboardConsumer {

    private final EmployeeRepository employeeRepository;

    @KafkaHandler
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        long headcount = employeeRepository.countByActiveTrue();
        log.info("[dashboard-service] Headcount now {} (latest hire: employeeId={}, {})",
                headcount, event.employeeId(), event.name());
    }

    @KafkaHandler
    public void onEmployeeDeleted(EmployeeDeletedEvent event) {
        long headcount = employeeRepository.countByActiveTrue();
        log.info("[dashboard-service] Headcount now {} (departed: employeeId={}, {})",
                headcount, event.employeeId(), event.name());
    }

    @KafkaHandler
    public void onEmployeeReactivated(EmployeeReactivatedEvent event) {
        long headcount = employeeRepository.countByActiveTrue();
        log.info("[dashboard-service] Headcount now {} (rejoined: employeeId={}, {})",
                headcount, event.employeeId(), event.name());
    }
}
