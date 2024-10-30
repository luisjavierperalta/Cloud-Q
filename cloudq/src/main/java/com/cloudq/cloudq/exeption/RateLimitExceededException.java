package com.cloudq.cloudq.exeption;


/**
 * Exception thrown when the rate limit is exceeded for a user.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
