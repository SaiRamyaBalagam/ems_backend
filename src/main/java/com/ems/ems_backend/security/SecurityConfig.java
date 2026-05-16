package com.ems.ems_backend.security;

import com.ems.ems_backend.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // Everyone can read everything
                        .requestMatchers(HttpMethod.GET,
                                "/api/employees/**", "/api/departments/**", "/api/attendance/**",
                                "/api/leaves/**", "/api/salaries/**", "/api/dashboard/**")
                                .hasAnyRole("ADMIN", "HR", "FINANCE", "MANAGER", "EMPLOYEE")

                        // Everyone can apply leave
                        .requestMatchers(HttpMethod.POST, "/api/leaves/**")
                                .hasAnyRole("ADMIN", "HR", "FINANCE", "MANAGER", "EMPLOYEE")

                        // Approve/reject leave — ADMIN, HR, MANAGER
                        .requestMatchers(HttpMethod.PUT, "/api/leaves/**")
                                .hasAnyRole("ADMIN", "HR", "MANAGER")

                        // Delete leave — ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/leaves/**")
                                .hasRole("ADMIN")

                        // Employees + Departments + Attendance: add/edit — ADMIN, HR, MANAGER
                        .requestMatchers(HttpMethod.POST, "/api/employees/**", "/api/departments/**", "/api/attendance/**")
                                .hasAnyRole("ADMIN", "HR", "MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**", "/api/departments/**", "/api/attendance/**")
                                .hasAnyRole("ADMIN", "HR", "MANAGER")

                        // Employees + Departments + Attendance: delete — ADMIN, HR
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**", "/api/departments/**", "/api/attendance/**")
                                .hasAnyRole("ADMIN", "HR")

                        // Salaries: add/edit/mark paid — ADMIN, FINANCE
                        .requestMatchers(HttpMethod.POST, "/api/salaries/**")
                                .hasAnyRole("ADMIN", "FINANCE")
                        .requestMatchers(HttpMethod.PUT, "/api/salaries/**")
                                .hasAnyRole("ADMIN", "FINANCE")

                        // Salaries: delete — ADMIN only
                        .requestMatchers(HttpMethod.DELETE, "/api/salaries/**")
                                .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
