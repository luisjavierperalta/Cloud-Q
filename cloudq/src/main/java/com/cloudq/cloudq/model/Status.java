package com.cloudq.cloudq.model;

/**
 * Enum representing the status of an optimization request.
 */
public enum Status {
    SUCCESS("Success"),
    FAILURE("Failure"),
    PENDING("Pending");

    private final String description;

    Status(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}