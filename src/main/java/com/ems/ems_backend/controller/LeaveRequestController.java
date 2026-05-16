package com.ems.ems_backend.controller;

import com.ems.ems_backend.dto.LeaveApplyRequest;
import com.ems.ems_backend.dto.LeaveStatusRequest;
import com.ems.ems_backend.model.LeaveRequest;
import com.ems.ems_backend.service.LeaveRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequest> applyLeave(@RequestBody LeaveApplyRequest request,
                                                    Authentication authentication) {
        LeaveRequest leaveRequest = leaveRequestService.applyLeave(
                request.getEmployeeId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getLeaveType(),
                request.getReason(),
                authentication.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(leaveRequest);
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaves() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaves());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeavesByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(leaveRequestService.getLeavesByEmployee(employeeId));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<LeaveRequest> updateLeaveStatus(@PathVariable Long id,
                                                          @RequestBody LeaveStatusRequest request) {
        return ResponseEntity.ok(leaveRequestService.updateLeaveStatus(id, request.getStatus()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long id) {
        leaveRequestService.deleteLeave(id);
        return ResponseEntity.ok("Leave request deleted successfully");
    }
}
