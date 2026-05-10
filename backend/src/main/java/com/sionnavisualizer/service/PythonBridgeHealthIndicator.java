package com.sionnavisualizer.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("pythonBridgeHealthIndicator")
public class PythonBridgeHealthIndicator implements HealthIndicator {

    private final SimulationService simulationService;

    public PythonBridgeHealthIndicator(SimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @Override
    public Health health() {
        if (simulationService.isPythonBridgeWarm()) {
            return Health.up().withDetail("python-bridge", "warm").build();
        } else {
            return Health.up().withDetail("python-bridge", "cold").build();
        }
    }
}
