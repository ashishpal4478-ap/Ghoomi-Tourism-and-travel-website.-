package com.ghoomi.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an outbound email (e.g. OTP verification email) fails to send
 * due to SMTP connection issues or downstream delivery errors.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class EmailDeliveryException extends RuntimeException {

    public EmailDeliveryException(String message) {
        super(message);
    }

    public EmailDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}
