package com.ems.ems_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class AttendanceRequest {
    private Long employeeId;
    private LocalDate date;
    private String status;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
}
