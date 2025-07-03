package com.blog.afaq.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;

    @Async
    public void sendWelcomeEmail(String recipientEmail, String password, String role) {
        String subject = "Welcome to Leggy - Your Account Details";

        String body = """
                    <html>
                    <head>
                        <style>
                            body {
                                font-family: 'Arial', sans-serif;
                                background-color: #F9F9F9;
                                margin: 0;
                                padding: 0;
                            }
                            .container {
                                max-width: 600px;
                                margin: 20px auto;
                                background-color: #FFFFFF;
                                border-radius: 10px;
                                padding: 25px;
                                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
                            }
                            .header {
                                background-color: #E64A19;
                                padding: 20px;
                                text-align: center;
                                color: #FFFFFF;
                                font-size: 24px;
                                font-weight: bold;
                                border-top-left-radius: 10px;
                                border-top-right-radius: 10px;
                            }
                            .content {
                                padding: 20px;
                                text-align: center;
                                color: #333333;
                                font-size: 16px;
                                line-height: 1.6;
                            }
                            .content h2 {
                                color: #388E3C;
                                font-size: 22px;
                                margin-bottom: 10px;
                            }
                            .credentials {
                                font-weight: bold;
                                text-align: center;
                                margin: 20px 0;
                                font-size: 18px;
                            }
                            .footer {
                                text-align: center;
                                margin-top: 20px;
                                font-size: 14px;
                                color: #666666;
                            }
                            .footer a {
                                color: #E64A19;
                                text-decoration: none;
                                font-weight: bold;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                Welcome to Leggy! 
                            </div>
                            <div class="content">
                                <h2>Dear %s,</h2>
                                <p>Your account has been successfully created as a <strong>%s</strong>.</p>
                                <p>Below are your login details:</p>
                                
                                <div class="credentials">
                                    👤 <strong>Email:</strong> %s<br>
                                    🔑 <strong>Password:</strong> %s
                                </div>

                                <p>🔒 For security reasons, we strongly recommend changing your password after logging in.</p>
                                <p>Thank you for joining <strong>blog</strong>! If you have any questions, feel free to contact us.</p>
                            </div>
                            <div class="footer">
                                <p>Best regards,<br><strong>Leggy Team</strong></p>
                                <p>📧 <a href="mailto:support@blog.com">Contact Support</a> | 🌍 <a href="https://blog.com">Visit Website</a></p>
                            </div>
                        </div>
                    </body>
                    </html>
                """.formatted(recipientEmail, role, recipientEmail, password);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    @Async
    public void sendEmailConfirmation(String recipientEmail, String confirmationLink) {
        String subject = "📧 Confirm Your Email - Leggy";
        String body = """
                    <html>
                    <body>
                        <h2>Hello %s,</h2>
                        <p>Thank you for registering on Leggy!</p>
                        <p>Please confirm your email by clicking the button below:</p>
                        <p><a href="%s" style="background-color: #E64A19; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">Confirm Email</a></p>
                        <p>This link will expire in 24 hours.</p>
                        <p>Best regards, <br> Leggy Team</p>
                    </body>
                    </html>
                """.formatted(recipientEmail, confirmationLink);

        try {
            sendHtmlEmail(recipientEmail, subject, body);
        } catch (RuntimeException e) {
            log.error("Failed to send confirmation email to {}: {}", recipientEmail, e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @Async
    public void sendResetPasswordCode(String recipientEmail, String code) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Recipient email must not be null or empty.");
        }

        String subject = "🔐 Your Blog Password Reset Code";

        String resetCode = (code != null && !code.isBlank()) ? code : "N/A";

        String body = String.format("""
        <html>
            <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="background-color: #ffffff; padding: 20px; border-radius: 8px; max-width: 600px; margin: auto;">
                    <h2 style="color: #333;">Reset Your Password</h2>
                    <p style="font-size: 16px;">Here is your one-time password (OTP) to reset your password:</p>
                    <h1 style="color: #E64A19; font-size: 36px;">%s</h1>
                    <p style="font-size: 14px; color: #555;">This code is valid for 1 minute.</p>
                    <p style="font-size: 14px; color: #777;">If you didn’t request a password reset, you can safely ignore this email.</p>
                    <p style="font-size: 14px; color: #555;">— Blog Team</p>
                </div>
            </body>
        </html>
        """, resetCode);

        sendHtmlEmail(recipientEmail, subject, body);
    }
    public void sendHtmlEmailTo(String to, String subject, String htmlContent, String replyTo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@yourdomain.com"); // your verified sender
            if (replyTo != null && !replyTo.isEmpty()) {
                helper.setReplyTo(replyTo);
            }

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }


}