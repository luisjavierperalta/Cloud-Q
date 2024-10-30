package com.cloudq.cloudq.repository;


import com.cloudq.cloudq.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Custom query to retrieve logs by user ID
    List<AuditLog> findByUserId(Long userId);

    // Custom query to retrieve logs by action type
    List<AuditLog> findByAction(String action);

    // Custom query to retrieve logs by timestamp range
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Delete old audit logs (for archival/purging)
    void deleteByTimestampBefore(LocalDateTime cutoffDate);
}
