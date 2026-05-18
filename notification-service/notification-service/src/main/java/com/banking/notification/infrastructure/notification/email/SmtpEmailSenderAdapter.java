package com.banking.notification.infrastructure.notification.email;

import com.banking.notification.application.port.out.EmailSenderPort;
import com.banking.notification.domain.model.Notification;
import com.banking.notification.infrastructure.notification.template.NotificationTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailSenderAdapter implements EmailSenderPort {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailSenderAdapter.class);

    private final JavaMailSender javaMailSender;
    private final NotificationTemplateBuilder notificationTemplateBuilder;

    public SmtpEmailSenderAdapter(
            JavaMailSender javaMailSender,
            NotificationTemplateBuilder notificationTemplateBuilder
    ) {
        this.javaMailSender = javaMailSender;
        this.notificationTemplateBuilder = notificationTemplateBuilder;
    }

    @Override
    public void send(Notification notification) {
        String subject = notificationTemplateBuilder.buildSubject(notification);
        String body = notificationTemplateBuilder.buildBody(notification);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(notification.getRecipient().email());
        message.setSubject(subject);
        message.setText(body);

        log.info(
                "[NOTIFICATION] Sending email notification. notificationId={}, transferId={}, type={}, recipient={}",
                notification.getId().value(),
                notification.getTransferId(),
                notification.getType(),
                notification.getRecipient().email()
        );

        javaMailSender.send(message);

        log.info(
                "[NOTIFICATION] Email notification sent successfully. notificationId={}, transferId={}",
                notification.getId().value(),
                notification.getTransferId()
        );
    }
}