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
                        // All three roles can read everything
                        .requestMatchers(HttpMethod.GET, "/api/employees/**", "/api/departments/**", "/api/attendance/**", "/api/leaves/**", "/api/salaries/**", "/api/dashboard/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        // Employees and managers can apply leave
                        .requestMatchers(HttpMethod.POST, "/api/leaves/**").hasAnyRole("ADMIN", "MANAGER", "EMPLOYEE")
                        // Managers can approve/reject leaves
                        .requestMatchers(HttpMethod.PUT, "/api/leaves/**").hasAnyRole("ADMIN", "MANAGER")
                        // Only admin can create employees, departments, attendance records, salaries
                        .requestMatchers(HttpMethod.POST, "/api/employees/**", "/api/departments/**", "/api/attendance/**", "/api/salaries/**").hasRole("ADMIN")
                        // Only admin can update employees, departments, attendance, salaries
                        .requestMatchers(HttpMethod.PUT, "/api/employees/**", "/api/departments/**", "/api/attendance/**", "/api/salaries/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/employees/**", "/api/departments/**", "/api/attendance/**", "/api/leaves/**", "/api/salaries/**").hasRole("ADMIN")
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
