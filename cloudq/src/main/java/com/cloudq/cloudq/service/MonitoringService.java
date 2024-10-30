package com.cloudq.cloudq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;

import javax.annotation.PostConstruct;

@Service
@RestController
public class MonitoringService {

    private final MeterRegistry meterRegistry;

    // Counter to track the number of requests processed
    private Counter requestCounter;

    // Gauge to track the current active users
    private int activeUsers = 0;

    @Autowired
    public MonitoringService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        // Initialize counters and gauges
        requestCounter = Counter.builder("requests_total")
                .description("Total number of requests processed")
                .register(meterRegistry);

        // Register a gauge to track active users
        Gauge.builder("active_users", this, MonitoringService::getActiveUsers)
                .description("Current number of active users")
                .register(meterRegistry);
    }

    // Increment the request counter
    public void incrementRequestCount() {
        requestCounter.increment();
    }

    // Increment active users count
    public void userLoggedIn() {
        activeUsers++;
    }

    // Decrement active users count
    public void userLoggedOut() {
        if (activeUsers > 0) {
            activeUsers--;
        }
    }

    // Method to get the number of active users
    public int getActiveUsers() {
        return activeUsers;
    }

    // Endpoint to get monitoring metrics (for example, you can expose this to Prometheus)
    @GetMapping("/metrics")
    public String getMetrics() {
        return "Active Users: " + activeUsers + ", Total Requests: " + requestCounter.count();
    }
}