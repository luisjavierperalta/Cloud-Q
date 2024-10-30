package com.cloudq.cloudq.service;


import com.cloudq.cloudq.exeption.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {

    // In-memory storage for request counts per user
    private final Map<String, RateLimitInfo> userRequests = new ConcurrentHashMap<>();

    // Load rate-limiting configurations from application properties
    @Value("${rate.limit.maxRequests:100}")
    private int maxRequests;

    @Value("${rate.limit.windowDuration:1}") // Window duration in minutes
    private int windowDuration;

    public boolean isAllowed(String userId) {
        Instant now = Instant.now();
        RateLimitInfo rateLimitInfo = userRequests.computeIfAbsent(userId, id -> new RateLimitInfo(now));

        synchronized (rateLimitInfo) {
            // Reset count if the window has expired
            if (Duration.between(rateLimitInfo.startTime, now).toMinutes() >= windowDuration) {
                rateLimitInfo.startTime = now;
                rateLimitInfo.requestCount.set(0);
            }

            if (rateLimitInfo.requestCount.incrementAndGet() > maxRequests) {
                throw new RateLimitExceededException("Rate limit exceeded for user " + userId);
            }
        }

        return true; // Allowed if rate limit is not exceeded
    }

    private static class RateLimitInfo {
        Instant startTime;
        AtomicInteger requestCount;

        RateLimitInfo(Instant startTime) {
            this.startTime = startTime;
            this.requestCount = new AtomicInteger(0);
        }
    }
}