package com.ems.ems_backend.dto;

import com.ems.ems_backend.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {
    private String username;
    private String password;
    private Role role;
    private Long employeeId;
}
