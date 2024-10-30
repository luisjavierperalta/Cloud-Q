package com.cloudq.cloudq.service;


import com.cloudq.cloudq.model.AuditLog;
import com.cloudq.cloudq.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Asynchronously logs an audit event.
     *
     * @param userId    ID of the user who performed the action
     * @param action    The action that was performed (e.g., "USER_LOGIN", "DATA_UPDATE")
     * @param details   Additional details about the action
     * @param ipAddress The IP address from where the action was performed
     */
    @Async
    public void logEvent(Long userId, String action, String details, String ipAddress) {
        // Sanitize details to mask sensitive data
        String sanitizedDetails = sanitizeDetails(details);

        // Create and save audit log
        AuditLog auditLog = new AuditLog(userId, action, sanitizedDetails, ipAddress, LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    /**
     * Sanitize sensitive data in audit details.
     * @param details Details of the audit log entry
     * @return Sanitized details with sensitive information masked
     */
    private String sanitizeDetails(String details) {
        if (details == null) return null;
        // Example: Mask password information
        return details.replaceAll("(?i)password=\\S+", "password=*****");
    }
}