package com.cloudq.cloudq.service;

public class Notification {

    private String recipient; // Email recipient
    private String subject;   // Email subject
    private String message;   // Email message body

    // Constructor
    public Notification(String recipient, String subject, String message) {
        this.recipient = recipient;
        this.subject = subject;
        this.message = message;
    }

    // Getter for recipient
    public String getRecipient() {
        return recipient;
    }

    // Getter for subject
    public String getSubject() {
        return subject;
    }

    // Getter for message
    public String getMessage() {
        return message;
    }

    // Optionally, you can also add setters if you need to modify these fields
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
