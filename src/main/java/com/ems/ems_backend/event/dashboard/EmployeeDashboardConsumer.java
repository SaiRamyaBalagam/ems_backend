package com.ems.ems_backend.event.dashboard;

import com.ems.ems_backend.event.EmployeeCreatedEvent;
import com.ems.ems_backend.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Stands in for a separate dashboard/read-model microservice. Its own
 * consumer group, independent of notification-service and audit-service.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeDashboardConsumer {

    private final EmployeeRepository employeeRepository;

    @KafkaListener(topics = "employee-events", groupId = "dashboard-service")
    public void onEmployeeCreated(EmployeeCreatedEvent event) {
        long headcount = employeeRepository.count();
        log.info("[dashboard-service] Headcount now {} (latest hire: employeeId={}, {})",
                headcount, event.employeeId(), event.name());
    }
}
