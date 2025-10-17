package com.production.microservices.microservicea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security Configuration to disable authentication for development/testing purposes.
 * 
 * WARNING: This configuration disables all security measures.
 * Do NOT use this configuration in production environments.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection
            .csrf(csrf -> csrf.disable())
            // Disable frame options (allows embedding in frames)
            .headers(headers -> headers.frameOptions().disable())
            // Permit all requests without authentication
            .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
            )
            // Disable HTTP Basic authentication
            .httpBasic(httpBasic -> httpBasic.disable())
            // Disable form login
            .formLogin(formLogin -> formLogin.disable());
            
        return http.build();
    }
}