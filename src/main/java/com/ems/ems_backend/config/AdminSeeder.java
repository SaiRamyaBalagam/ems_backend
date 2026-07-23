package com.ems.ems_backend.config;

import com.ems.ems_backend.model.Role;
import com.ems.ems_backend.model.User;
import com.ems.ems_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * A fresh DB otherwise has no users and no way to create one (employees
 * only get accounts once an ADMIN/HR/MANAGER adds them). Seeds a single
 * bootstrap ADMIN so a new environment isn't a dead end.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private static final String DEFAULT_ADMIN_USERNAME = "admin@ems.local";
    private static final String DEFAULT_ADMIN_PASSWORD = "Admin@123";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User();
        admin.setUsername(DEFAULT_ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.warn("No users found in the database — seeded a bootstrap admin account " +
                "(username={}, password={}). Change this password after logging in.",
                DEFAULT_ADMIN_USERNAME, DEFAULT_ADMIN_PASSWORD);
    }
}
