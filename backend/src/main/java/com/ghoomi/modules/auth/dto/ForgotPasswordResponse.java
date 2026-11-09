package com.ghoomi.modules.auth.dto;

public class ForgotPasswordResponse {

    private String message;
    private int retryAfterSeconds;

    public ForgotPasswordResponse() {}

    public ForgotPasswordResponse(String message, int retryAfterSeconds) {
        this.message = message;
        this.retryAfterSeconds = retryAfterSeconds;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRetryAfterSeconds() { return retryAfterSeconds; }
    public void setRetryAfterSeconds(int retryAfterSeconds) { this.retryAfterSeconds = retryAfterSeconds; }
}

