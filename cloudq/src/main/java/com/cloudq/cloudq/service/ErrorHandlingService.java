package com.cloudq.cloudq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Service
public class ErrorHandlingService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingService.class);

    /**
     * Handles application-specific exceptions and logs error details.
     *
     * @param ex The caught exception.
     * @param webRequest The current web request context (useful for retrieving URL, headers, etc.).
     */
    public void handleApplicationError(Exception ex, WebRequest webRequest) {
        String errorDetails = String.format("Exception in %s at %s: %s",
                webRequest.getDescription(false),
                LocalDateTime.now(),
                ex.getMessage());

        // Log the error with detailed information
        logger.error(errorDetails, ex);

        // Optionally send alerts for critical errors (e.g., email, monitoring service)
        if (isCriticalError(ex)) {
            sendAlert(ex, webRequest);
        }
    }

    /**
     * Handles user-specific errors, logs them as warnings, and provides user-friendly messages.
     *
     * @param ex The caught exception.
     * @param userMessage Custom message for the end user.
     * @param webRequest The current web request context.
     * @return User-friendly error message.
     */
    public String handleUserError(Exception ex, String userMessage, WebRequest webRequest) {
        String errorDetails = String.format("User error in %s at %s: %s",
                webRequest.getDescription(false),
                LocalDateTime.now(),
                ex.getMessage());

        // Log user errors as warnings
        logger.warn(errorDetails, ex);

        // Return a user-friendly message for the client
        return userMessage;
    }

    /**
     * Determines whether an error should be categorized as critical.
     * Critical errors may include database connection failures, authentication errors, etc.
     *
     * @param ex The caught exception.
     * @return True if the error is critical, false otherwise.
     */
    private boolean isCriticalError(Exception ex) {
        // Define critical error types
        return ex instanceof NullPointerException || ex instanceof IllegalStateException;
    }

    /**
     * Sends an alert for critical errors, which could involve notifying administrators
     * or integrating with external monitoring/alerting systems.
     *
     * @param ex The critical exception.
     * @param webRequest The current web request context.
     */
    private void sendAlert(Exception ex, WebRequest webRequest) {
        // Implement alert notification logic (e.g., send email, push to monitoring tool)
        String alertMessage = String.format("Critical error occurred in %s: %s",
                webRequest.getDescription(false),
                ex.getMessage());

        // For example, integrate with an alerting service or send an email to the admin team
        logger.error("Alert sent: " + alertMessage);
        // In a real-world scenario, implement code to send alerts via email, monitoring API, etc.
    }
}

