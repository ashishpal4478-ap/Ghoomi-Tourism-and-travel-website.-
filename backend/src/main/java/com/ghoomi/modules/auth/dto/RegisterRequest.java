package com.ghoomi.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Request body for POST /api/v1/auth/register.
 *
 * <p>accountType must be either "traveller" or "partner_application".
 * The backend ALWAYS assigns USER role — the accountType is only stored
 * as metadata for partner workflow routing. Never accept roles from clients.
 */
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 200, message = "Name must be 200 characters or fewer")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "A valid email address is required")
    @Size(max = 254, message = "Email must be 254 characters or fewer")
    private String email;

    /** E.164-normalised phone number, e.g. +919876543210 */
    @Pattern(regexp = "^\\+[1-9]\\d{6,14}$", message = "Phone must be in E.164 format, e.g. +919876543210")
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
    private String password;

    /** "traveller" or "partner_application". Default: "traveller". */
    private String accountType = "traveller";

    // Optional partner fields — only present when accountType=partner_application
    private java.util.List<String> businessCategories;
    private BusinessDetails businessDetails;

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public java.util.List<String> getBusinessCategories() { return businessCategories; }
    public void setBusinessCategories(java.util.List<String> businessCategories) {
        this.businessCategories = businessCategories;
    }

    public BusinessDetails getBusinessDetails() { return businessDetails; }
    public void setBusinessDetails(BusinessDetails businessDetails) {
        this.businessDetails = businessDetails;
    }

    // ── Nested DTO ───────────────────────────────────────────────────────────

    public static class BusinessDetails {
        private String businessName;
        private String businessType;
        private String city;

        public String getBusinessName() { return businessName; }
        public void setBusinessName(String businessName) { this.businessName = businessName; }

        public String getBusinessType() { return businessType; }
        public void setBusinessType(String businessType) { this.businessType = businessType; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
    }
}

