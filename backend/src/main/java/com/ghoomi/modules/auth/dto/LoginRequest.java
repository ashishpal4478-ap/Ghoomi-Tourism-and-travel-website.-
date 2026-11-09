package com.ghoomi.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/v1/auth/login.
 */
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "A valid email address is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private boolean rememberMe = false;

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isRememberMe() { return rememberMe; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
}

