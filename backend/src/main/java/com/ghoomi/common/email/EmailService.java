package com.ghoomi.common.email;

import com.ghoomi.common.exception.EmailDeliveryException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * Service responsible for composing and sending transactional outbound emails
 * using {@link JavaMailSender}.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${app.mail.from:noreply@ghoomi.com}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    /**
     * Dispatches a 6-digit OTP verification code for password reset.
     * Generates a professional HTML email with a plain-text fallback.
     * Throws {@link EmailDeliveryException} if sending fails.
     *
     * @param toEmail destination email
     * @param otp 6-digit verification code
     * @param expirationMinutes code validity duration
     */
    public void sendPasswordResetOtp(String toEmail, String otp, int expirationMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, "Ghoomi Travel");
            helper.setTo(toEmail);
            helper.setSubject("Your Ghoomi Password Reset Code");

            String textBody = buildPlainTextBody(otp, expirationMinutes);
            String htmlBody = buildHtmlBody(otp, expirationMinutes);

            // True multipart/alternative (plain-text fallback + rich HTML)
            helper.setText(textBody, htmlBody);

            mailSender.send(message);
            log.info("Password reset OTP email dispatched successfully to: {}", maskEmail(toEmail));
        } catch (MailException | MessagingException | UnsupportedEncodingException ex) {
            log.error("Failed to send password reset OTP to: {}. Reason: {}", maskEmail(toEmail), ex.getMessage());
            throw new EmailDeliveryException("Failed to send verification email. Please try again shortly.", ex);
        }
    }

    private String buildPlainTextBody(String otp, int expirationMinutes) {
        return """
                Hello,

                You recently requested to reset your password for your Ghoomi account.
                Your 6-digit verification code is:

                %s

                This code will expire in %d minutes.
                For your security, DO NOT share this code with anyone. Ghoomi staff will never ask for your verification code.

                If you did not request a password reset, you can safely ignore this email. Your account remains secure.

                Best regards,
                The Ghoomi Team
                https://ghoomi.com
                """.formatted(otp, expirationMinutes);
    }

    private String buildHtmlBody(String otp, int expirationMinutes) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Ghoomi Password Reset</title>
                </head>
                <body style="margin: 0; padding: 24px; background-color: #F8FAFC; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; color: #1E293B;">
                  <div style="max-width: 560px; margin: 0 auto; background: #FFFFFF; border-radius: 12px; overflow: hidden; border: 1px solid #E2E8F0; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);">
                    <div style="background: linear-gradient(135deg, #4F46E5 0%%, #4338CA 100%%); padding: 32px 24px; text-align: center;">
                      <h1 style="margin: 0; color: #FFFFFF; font-size: 26px; font-weight: 800; letter-spacing: -0.5px;">GHOOMI</h1>
                      <p style="margin: 6px 0 0; color: #E0E7FF; font-size: 14px;">Your Intelligent Travel Companion</p>
                    </div>
                    <div style="padding: 32px 24px;">
                      <h2 style="margin: 0 0 12px; font-size: 20px; font-weight: 700; color: #0F172A;">Password Reset Verification</h2>
                      <p style="margin: 0 0 20px; font-size: 15px; line-height: 1.6; color: #475569;">
                        We received a request to reset the password for your Ghoomi account. Use the verification code below to authorize the password reset:
                      </p>
                      <div style="background-color: #EEF2FF; border: 2px dashed #6366F1; border-radius: 10px; padding: 20px; text-align: center; margin: 24px 0;">
                        <span style="font-family: 'Courier New', Courier, monospace; font-size: 34px; font-weight: 800; letter-spacing: 8px; color: #312E81; display: inline-block;">
                          %s
                        </span>
                        <div style="margin-top: 8px; font-size: 13px; font-weight: 600; color: #4F46E5;">
                          Valid for %d minutes
                        </div>
                      </div>
                      <div style="background-color: #FEF2F2; border-left: 4px solid #EF4444; padding: 14px 16px; border-radius: 6px; margin: 24px 0;">
                        <div style="font-weight: 700; font-size: 13px; color: #991B1B; margin-bottom: 2px;">SECURITY WARNING</div>
                        <div style="font-size: 13px; line-height: 1.5; color: #B91C1C;">
                          Never share this verification code with anyone. Ghoomi staff will never ask for this code via email, phone, or messaging apps.
                        </div>
                      </div>
                      <p style="margin: 20px 0 0; font-size: 14px; line-height: 1.5; color: #64748B;">
                        If you did not request this password reset, please ignore this email. Your existing password will remain active and secure.
                      </p>
                    </div>
                    <div style="background-color: #F1F5F9; padding: 20px 24px; text-align: center; border-top: 1px solid #E2E8F0;">
                      <p style="margin: 0; font-size: 12px; color: #94A3B8;">
                        &copy; 2026 Ghoomi Travel Technologies. All rights reserved.
                      </p>
                    </div>
                  </div>
                </body>
                </html>
                """.formatted(otp, expirationMinutes);
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        if (name.length() <= 2) {
            return name.charAt(0) + "***" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }
}
