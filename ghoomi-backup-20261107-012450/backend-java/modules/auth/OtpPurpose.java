package com.ghoomi.modules.auth;

/**
 * Discriminator for OTP purpose — allows one table to handle
 * multiple OTP flows without ambiguity.
 */
public enum OtpPurpose {
    PASSWORD_RESET
}

