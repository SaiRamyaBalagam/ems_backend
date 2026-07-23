package com.ems.ems_backend.service;

import com.ems.ems_backend.event.LeaveApplied;
import com.ems.ems_backend.event.LeaveApproved;
import com.ems.ems_backend.event.LeaveRejected;
import com.ems.ems_backend.exception.ForbiddenException;
import com.ems.ems_backend.exception.ResourceNotFoundException;
import com.ems.ems_backend.model.Employee;
import com.ems.ems_backend.model.LeaveRequest;
import com.ems.ems_backend.model.LeaveStatus;
import com.ems.ems_backend.model.LeaveType;
import com.ems.ems_backend.model.Role;
import com.ems.ems_backend.model.User;
import com.ems.ems_backend.repository.EmployeeRepository;
import com.ems.ems_backend.repository.LeaveRequestRepository;
import com.ems.ems_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeaveRequestService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public LeaveRequest applyLeave(Long employeeId, LocalDate startDate, LocalDate endDate,
                                   String leaveType, String reason, String username) {

        User requestingUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // EMPLOYEE can only apply leave for themselves
        if (requestingUser.getRole() == Role.EMPLOYEE) {
            if (requestingUser.getEmployee() == null) {
                throw new ForbiddenException("Your user account is not linked to an employee record. Contact admin.");
            }
            if (!requestingUser.getEmployee().getId().equals(employeeId)) {
                throw new ForbiddenException("You can only apply leave for yourself");
            }
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(employee);
        leaveRequest.setStartDate(startDate);
        leaveRequest.setEndDate(endDate);
        leaveRequest.setLeaveType(LeaveType.valueOf(leaveType.toUpperCase()));
        leaveRequest.setReason(reason);
        leaveRequest.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        eventPublisher.publishEvent(new LeaveApplied(
                saved.getId(),
                employeeId,
                saved.getLeaveType().name(),
                saved.getStartDate(),
                saved.getEndDate(),
                saved.getReason(),
                Instant.now()
        ));

        return saved;
    }

    public List<LeaveRequest> getAllLeaves() {
        return leaveRequestRepository.findAll();
    }

    public List<LeaveRequest> getLeavesByEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with id: " + employeeId);
        }
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public LeaveRequest updateLeaveStatus(Long id, String status) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with id: " + id));

        LeaveStatus newStatus = LeaveStatus.valueOf(status.toUpperCase());
        leaveRequest.setStatus(newStatus);
        LeaveRequest saved = leaveRequestRepository.save(leaveRequest);

        Long employeeId = saved.getEmployee().getId();
        if (newStatus == LeaveStatus.APPROVED) {
            eventPublisher.publishEvent(new LeaveApproved(saved.getId(), employeeId, Instant.now()));
        } else if (newStatus == LeaveStatus.REJECTED) {
            eventPublisher.publishEvent(new LeaveRejected(saved.getId(), employeeId, Instant.now()));
        }

        return saved;
    }

    public void deleteLeave(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Leave request not found with id: " + id);
        }
        leaveRequestRepository.deleteById(id);
    }
}
