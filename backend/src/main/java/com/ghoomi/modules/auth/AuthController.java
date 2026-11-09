package com.ghoomi.modules.auth;

import com.ghoomi.common.response.ApiResponse;
import com.ghoomi.modules.auth.dto.CurrentUserResponse;
import com.ghoomi.modules.auth.dto.LoginRequest;
import com.ghoomi.modules.auth.dto.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication endpoints — all under /api/v1/auth.
 *
 * <p>All endpoints return the standard {@link ApiResponse} envelope
 * compatible with the frontend AuthApiError contract.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // ── POST /api/v1/auth/register ────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        CurrentUserResponse user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Account created successfully.", user));
    }

    // ── POST /api/v1/auth/login ───────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {
        CurrentUserResponse user = authService.login(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", user));
    }

    // ── GET /api/v1/auth/me ───────────────────────────────────────────────────

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CurrentUserResponse>> me(Authentication authentication) {
        CurrentUserResponse user = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    // ── POST /api/v1/auth/logout ──────────────────────────────────────────────

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        // Invalidate session explicitly
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully."));
    }

    // ── GET /api/v1/auth/csrf ─────────────────────────────────────────────────
    // Calling this endpoint forces Spring Security to initialize the CSRF token
    // and set the XSRF-TOKEN cookie so the frontend can read it for POST requests.

    @GetMapping("/csrf")
    public ResponseEntity<ApiResponse<Void>> csrf() {
        return ResponseEntity.ok(ApiResponse.success("CSRF token initialized."));
    }

    // ── POST /api/v1/auth/oauth/google/start ──────────────────────────────────
    // Google OAuth is not configured. Return a clear 503 rather than failing silently.

    @PostMapping("/oauth/google/start")
    public ResponseEntity<ApiResponse<Void>> googleStart() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error("Google authentication is not configured for this deployment."));
    }

    // ── POST /api/v1/auth/password/forgot ─────────────────────────────────────

    @PostMapping("/password/forgot")
    public ResponseEntity<ApiResponse<Void>> forgotPassword() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "Password recovery via email is not yet configured. Please contact support."));
    }

    // ── POST /api/v1/auth/password/verify-otp ────────────────────────────────

    @PostMapping("/password/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "Password recovery via OTP is not yet configured. Please contact support."));
    }

    // ── POST /api/v1/auth/password/reset ─────────────────────────────────────

    @PostMapping("/password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        "Password reset is not yet configured. Please contact support."));
    }
}

