package com.ems.ems_backend.repository;

import com.ems.ems_backend.model.LeaveRequest;
import com.ems.ems_backend.model.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeId(Long employeeId);
    long countByStatus(LeaveStatus status);
}
