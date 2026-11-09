package com.ghoomi.common.email;

import com.ghoomi.common.exception.EmailDeliveryException;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    private static final String FROM_ADDRESS = "noreply@ghoomi.com";
    private static final String RECIPIENT = "traveler@example.com";
    private static final String OTP = "482910";
    private static final int EXPIRY_MINUTES = 10;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, FROM_ADDRESS);
    }

    @Test
    void sendPasswordResetOtp_successfulDelivery_verifiesMimeMessageDetails() throws Exception {
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendPasswordResetOtp(RECIPIENT, OTP, EXPIRY_MINUTES);

        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        MimeMessage sentMessage = captor.getValue();
        assertNotNull(sentMessage);

        // 1. Recipient check
        Address[] recipients = sentMessage.getRecipients(Message.RecipientType.TO);
        assertNotNull(recipients);
        assertEquals(1, recipients.length);
        assertEquals(RECIPIENT, recipients[0].toString());

        // 2. Subject check
        assertEquals("Your Ghoomi Password Reset Code", sentMessage.getSubject());

        // 3. Sender check
        Address[] from = sentMessage.getFrom();
        assertNotNull(from);
        assertTrue(from[0].toString().contains(FROM_ADDRESS));

        // 4. Content check: write message to stream to inspect raw multi-part content
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        sentMessage.writeTo(outputStream);
        String rawContent = outputStream.toString();

        // Must contain OTP
        assertTrue(rawContent.contains(OTP), "Email content must contain 6-digit OTP code");
        // Must contain expiration
        assertTrue(rawContent.contains("10 minutes"), "Email content must mention 10 minutes validity");
        // Must contain security advisory
        assertTrue(rawContent.contains("Never share") || rawContent.contains("DO NOT share"),
                "Email must contain security warning not to share code");
    }

    @Test
    void sendPasswordResetOtp_smtpFailure_throwsEmailDeliveryException() {
        Session session = Session.getInstance(new Properties());
        MimeMessage mimeMessage = new MimeMessage(session);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailSendException("SMTP connection timed out: smtp.gmail.com:587"))
                .when(mailSender).send(any(MimeMessage.class));

        EmailDeliveryException thrown = assertThrows(EmailDeliveryException.class, () ->
                emailService.sendPasswordResetOtp(RECIPIENT, OTP, EXPIRY_MINUTES)
        );

        // User-facing message must not leak internal host/port
        assertFalse(thrown.getMessage().contains("smtp.gmail.com"));
        assertFalse(thrown.getMessage().contains("587"));
        assertTrue(thrown.getMessage().contains("Failed to send verification email"));
    }
}
