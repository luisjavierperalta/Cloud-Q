package com.cloudq.cloudq.service;


import com.cloudq.cloudq.exeption.NotificationException;
import com.cloudq.cloudq.model.EmailNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    @Autowired
    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send a notification via email.
     *
     * @param notification the notification to send
     * @throws NotificationException if an error occurs while sending the notification
     */
    @Transactional(rollbackFor = NotificationException.class)
    public void sendEmailNotification(Notification notification) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(notification.getRecipient());
            message.setSubject(notification.getSubject());
            message.setText(notification.getMessage());
            mailSender.send(message);
            logger.info("Email sent to {}", notification.getRecipient());
        } catch (Exception e) {
            logger.error("Error sending email to {}: {}", notification.getRecipient(), e.getMessage());
            throw new NotificationException("Failed to send email notification to " + notification.getRecipient(), e);
        }
    }

    /**
     * Send multiple notifications via email.
     *
     * @param notifications a list of notifications to send
     * @throws NotificationException if any error occurs while sending the notifications
     */
    public void sendBulkEmailNotifications(List<Notification> notifications) {
        for (Notification notification : notifications) {
            sendEmailNotification(notification);
        }
    }

    /**
     * Other notification methods (SMS, push notifications, etc.) can be added here.
     */
}
