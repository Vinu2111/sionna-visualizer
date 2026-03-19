package com.sionnavisualizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Main entry point for the Sionna Visualizer backend application.
 * The @SpringBootApplication annotation enables auto-configuration,
 * component scanning, and configuration properties in one shot.
 */
@SpringBootApplication
public class SionnaVisualizerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SionnaVisualizerApplication.class, args);
    }

    /**
     * Register RestTemplate as a Spring-managed Bean.
     * By declaring it here, Spring can inject it anywhere in the application
     * using @Autowired or constructor injection — no need to create it manually.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
