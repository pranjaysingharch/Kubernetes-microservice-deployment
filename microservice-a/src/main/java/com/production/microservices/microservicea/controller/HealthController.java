package com.production.microservices.microservicea.controller;

import com.production.microservices.microservicea.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/health")
public class HealthController {
    
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);
    
    private final DataSource dataSource;
    private final ProductService productService;
    
    @Autowired
    public HealthController(DataSource dataSource, ProductService productService) {
        this.dataSource = dataSource;
        this.productService = productService;
    }
    
    /**
     * Liveness probe - should return 200 if the application is running
     * This is used by Kubernetes to know if the pod needs to be restarted
     */
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> liveness() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Application is running");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Readiness probe - should return 200 if the application is ready to serve traffic
     * This is used by Kubernetes to know if the pod should receive traffic
     */
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> readiness() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> checks = new HashMap<>();
        
        boolean isReady = true;
        
        // Check database connectivity
        try {
            try (Connection connection = dataSource.getConnection()) {
                boolean dbValid = connection.isValid(5); // 5 second timeout
                checks.put("database", dbValid ? "UP" : "DOWN");
                if (!dbValid) {
                    isReady = false;
                }
            }
        } catch (SQLException e) {
            logger.error("Database health check failed", e);
            checks.put("database", "DOWN");
            checks.put("databaseError", e.getMessage());
            isReady = false;
        }
        
        // Check if we can access the service layer
        try {
            productService.getTotalActiveProductsCount();
            checks.put("productService", "UP");
        } catch (Exception e) {
            logger.error("Product service health check failed", e);
            checks.put("productService", "DOWN");
            checks.put("serviceError", e.getMessage());
            isReady = false;
        }
        
        response.put("status", isReady ? "UP" : "DOWN");
        response.put("timestamp", LocalDateTime.now());
        response.put("checks", checks);
        
        return ResponseEntity.status(isReady ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                           .body(response);
    }
    
    /**
     * Startup probe - used by Kubernetes to know when the application has started
     * This is particularly useful for slow-starting applications
     */
    @GetMapping("/startup")
    public ResponseEntity<Map<String, Object>> startup() {
        Map<String, Object> response = new HashMap<>();
        
        // Check if application context is fully loaded and database is accessible
        try {
            try (Connection connection = dataSource.getConnection()) {
                boolean dbConnected = connection.isValid(10); // 10 second timeout for startup
                
                if (dbConnected) {
                    response.put("status", "UP");
                    response.put("timestamp", LocalDateTime.now());
                    response.put("message", "Application started successfully");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("status", "DOWN");
                    response.put("timestamp", LocalDateTime.now());
                    response.put("message", "Database connection not ready");
                    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
                }
            }
        } catch (SQLException e) {
            logger.error("Startup health check failed", e);
            response.put("status", "DOWN");
            response.put("timestamp", LocalDateTime.now());
            response.put("message", "Database connection failed");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }
}