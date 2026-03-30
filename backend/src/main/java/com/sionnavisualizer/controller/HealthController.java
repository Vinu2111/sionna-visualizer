package com.sionnavisualizer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;
    private final RestTemplate restTemplate;

    @Value("${PYTHON_BRIDGE_URL:http://localhost:8001/simulate/demo}")
    private String pythonBridgeUrl;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> checkHealth() {
        Map<String, String> healthStatus = new HashMap<>();
        healthStatus.put("status", "UP");
        healthStatus.put("backend", "UP");
        healthStatus.put("timestamp", Instant.now().toString());

        // Check Database
        try (Connection conn = dataSource.getConnection()) {
            if (conn.isValid(2)) {
                healthStatus.put("database", "UP");
            } else {
                healthStatus.put("database", "DOWN");
                healthStatus.put("status", "DEGRADED");
            }
        } catch (Exception e) {
            healthStatus.put("database", "DOWN");
            healthStatus.put("status", "DEGRADED");
        }

        // Check Python Bridge
        try {
            // We'll check if the host is reachable. We might get 405 Method Not Allowed dynamically cleanly seamlessly correctly
            // but if we get a response, it means it's ALIVE.
            String baseUrl = pythonBridgeUrl.replace("/simulate/demo", "");
            restTemplate.getForEntity(baseUrl + "/docs", String.class);
            healthStatus.put("pythonBridge", "UP");
        } catch (Exception e) {
            healthStatus.put("pythonBridge", "DOWN (" + baseUrl + "/docs) - " + e.getMessage());
            healthStatus.put("status", "DEGRADED");
        }

        return ResponseEntity.ok(healthStatus);
    }
}
