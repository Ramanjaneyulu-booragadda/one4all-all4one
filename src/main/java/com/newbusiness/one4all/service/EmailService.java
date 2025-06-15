package com.newbusiness.one4all.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.Body;
import software.amazon.awssdk.services.ses.model.Content;
import software.amazon.awssdk.services.ses.model.Destination;
import software.amazon.awssdk.services.ses.model.Message;
import software.amazon.awssdk.services.ses.model.SendEmailRequest;
import software.amazon.awssdk.services.ses.model.SendEmailResponse;
import software.amazon.awssdk.services.ses.model.SesException;

import java.util.Map;

@Service
@Slf4j
public class EmailService {

    @Value("${aws.ses.region}")
    private String awsRegion;

    @Value("${aws.ses.access-key}")
    private String awsAccessKey;

    @Value("${aws.ses.secret-key}")
    private String awsSecretKey;

    @Value("${aws.ses.verified-sender}")
    private String senderEmail;

    public boolean sendEmail(String to, String subject, String htmlBody, String textBody) {
        try (SesClient sesClient = SesClient.builder()
                .region(Region.of(awsRegion))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .build()) {

            Destination destination = Destination.builder().toAddresses(to).build();
            Content subjContent = Content.builder().data(subject).build();
            Content htmlContent = Content.builder().data(htmlBody).build();
            Content textContent = Content.builder().data(textBody).build();
            Body body = Body.builder().html(htmlContent).text(textContent).build();
            Message message = Message.builder().subject(subjContent).body(body).build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source(senderEmail)
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);
            log.info("Email sent to {} with messageId {}", to, response.messageId());
            return true;
        } catch (SesException e) {
            log.error("Failed to send email to {}: {}", to, e.awsErrorDetails().errorMessage());
            return false;
        }
    }

    // Example: send registration email
    public boolean sendRegistrationEmail(String to, String username) {
        String subject = "Welcome to One4All!";
        String htmlBody = "<h1>Welcome, " + username + "!</h1><p>Thank you for registering.</p>";
        String textBody = "Welcome, " + username + "! Thank you for registering.";
        return sendEmail(to, subject, htmlBody, textBody);
    }

    // Improved: send registration email with member details
    public boolean sendRegistrationEmail(String to, String username, Map<String, Object> data) {
        String subject = "Welcome to One4All!";
        String htmlBody = "<h2>Welcome to the One4All Family, " + username + "!</h2>"
                + "<p>Thank you for registering with us. Your account has been created successfully. Below are your login details:</p>"
                + "<ul>"
                + "<li><b>Member ID:</b> " + data.getOrDefault("MemberID", "") + "</li>"
                + "<li><b>Email:</b> " + data.getOrDefault("emailid", "") + "</li>"
                + "<li><b>Mobile:</b> " + data.getOrDefault("Mobile", "") + "</li>"
                + "</ul>"
                + "<p>If you have any questions or need assistance, please contact our support team.</p>"
                + "<p>Best regards,<br/>The One4All Team</p>";
        String textBody = "Welcome to the One4All Family, " + username + "!\n\n"
                + "Thank you for registering with us. Your account has been created successfully. Below are your login details:\n"
                + "Member ID: " + data.getOrDefault("MemberID", "") + "\n"
                + "Email: " + data.getOrDefault("emailid", "") + "\n"
                + "Mobile: " + data.getOrDefault("Mobile", "") + "\n\n"
                + "If you have any questions or need assistance, please contact our support team.\n"
                + "Best regards,\nThe One4All Team";
        return sendEmail(to, subject, htmlBody, textBody);
    }

    // Improved: send password reset email
    public boolean sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Password Reset Request - One4All";
        String htmlBody = "<p>Dear User,</p>"
                + "<p>We received a request to reset your password. Please click the link below to set a new password:</p>"
                + "<p><a href='" + resetLink + "'>Reset Your Password</a></p>"
                + "<p>If you did not request a password reset, please ignore this email. Your account will remain secure.</p>"
                + "<p>If you need help, contact our support team.</p>"
                + "<p>Best regards,<br/>The One4All Team</p>";
        String textBody = "Dear User,\n\n"
                + "We received a request to reset your password. Please use the link below to set a new password:\n"
                + resetLink + "\n\n"
                + "If you did not request a password reset, please ignore this email. Your account will remain secure.\n"
                + "If you need help, contact our support team.\n\n"
                + "Best regards,\nThe One4All Team";
        return sendEmail(to, subject, htmlBody, textBody);
    }

    // Send password reset success email
    public boolean sendPasswordResetSuccessEmail(String to) {
        String subject = "Your One4All Password Was Reset Successfully";
        String htmlBody = "<p>Dear User,</p>"
                + "<p>Your password has been reset successfully. If you did not perform this action, please contact our support team immediately.</p>"
                + "<p>Best regards,<br/>The One4All Team</p>";
        String textBody = "Dear User,\nYour password has been reset successfully. If you did not perform this action, please contact our support team immediately.\nBest regards,\nThe One4All Team";
        return sendEmail(to, subject, htmlBody, textBody);
    }
}
