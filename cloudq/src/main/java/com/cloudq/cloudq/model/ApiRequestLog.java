package com.cloudq.cloudq.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_request_logs")
public class ApiRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint", nullable = false, length = 100)
    private String endpoint;

    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;

    @Column(name = "response_status", nullable = false)
    private int responseStatus;

    @Column(name = "request_timestamp", nullable = false)
    private LocalDateTime requestTimestamp;

    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    // Constructors, Getters, and Setters

    public ApiRequestLog() {}

    public ApiRequestLog(String endpoint, String httpMethod, int responseStatus, LocalDateTime requestTimestamp, Long responseTimeMs, String clientIp) {
        this.endpoint = endpoint;
        this.httpMethod = httpMethod;
        this.responseStatus = responseStatus;
        this.requestTimestamp = requestTimestamp;
        this.responseTimeMs = responseTimeMs;
        this.clientIp = clientIp;
    }

    public Long getId() { return id; }

    public String getEndpoint() { return endpoint; }
    public void setEndpoint(String endpoint) { this.endpoint = endpoint; }

    public String getHttpMethod() { return httpMethod; }
    public void setHttpMethod(String httpMethod) { this.httpMethod = httpMethod; }

    public int getResponseStatus() { return responseStatus; }
    public void setResponseStatus(int responseStatus) { this.responseStatus = responseStatus; }

    public LocalDateTime getRequestTimestamp() { return requestTimestamp; }
    public void setRequestTimestamp(LocalDateTime requestTimestamp) { this.requestTimestamp = requestTimestamp; }

    public Long getResponseTimeMs() { return responseTimeMs; }
    public void setResponseTimeMs(Long responseTimeMs) { this.responseTimeMs = responseTimeMs; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    @Override
    public String toString() {
        return "ApiRequestLog{" +
                "id=" + id +
                ", endpoint='" + endpoint + '\'' +
                ", httpMethod='" + httpMethod + '\'' +
                ", responseStatus=" + responseStatus +
                ", requestTimestamp=" + requestTimestamp +
                ", responseTimeMs=" + responseTimeMs +
                ", clientIp='" + clientIp + '\'' +
                '}';
    }
}