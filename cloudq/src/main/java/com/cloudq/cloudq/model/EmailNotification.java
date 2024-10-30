package com.cloudq.cloudq.model;


public class EmailNotification {

    private String recipient; // Email recipient
    private String subject;   // Email subject
    private String message;   // Email message body

    // Constructor
    public EmailNotification(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    // Getters
    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    // Setters (optional)
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

