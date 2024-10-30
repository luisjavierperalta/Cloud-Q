package com.cloudq.cloudq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {

    private final Logger logger;

    public LoggingService() {
        this.logger = LoggerFactory.getLogger(LoggingService.class);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void warn(String message) {
        logger.warn(message);
    }

    public void error(String message) {
        logger.error(message);
    }

    public void debug(String message) {
        logger.debug(message);
    }

    public void trace(String message) {
        logger.trace(message);
    }

    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

    // Example method to log specific events
    public void logUserAction(String username, String action) {
        logger.info("User '{}' performed action: {}", username, action);
    }
}