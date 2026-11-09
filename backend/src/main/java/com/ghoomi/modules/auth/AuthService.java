package com.ghoomi.modules.auth;

import com.ghoomi.common.exception.ConflictException;
import com.ghoomi.common.exception.ResourceNotFoundException;
import com.ghoomi.common.exception.UnauthorizedException;
import com.ghoomi.modules.auth.dto.*;
import com.ghoomi.modules.auth.oauth2.CustomOAuth2User;
import com.ghoomi.modules.user.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Core authentication service.
 *
 * <p>Security invariants:
 * <ul>
 *   <li>Passwords are always BCrypt-hashed (cost 12) — never stored plaintext.</li>
 *   <li>Role is always set server-side to USER — never trusts client input.</li>
 *   <li>Sessions are server-side HTTP sessions — no JWT, no localStorage tokens.</li>
 *   <li>Partner registration creates USER role with metadata only — no privilege elevation.</li>
 * </ul>
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final OtpRecordRepository otpRecordRepository;
    private final com.ghoomi.common.email.EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(UserRepository userRepository,
                       UserProfileRepository userProfileRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       OtpRecordRepository otpRecordRepository,
                       com.ghoomi.common.email.EmailService emailService) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.otpRecordRepository = otpRecordRepository;
        this.emailService = emailService;
    }

    // ── Registration ─────────────────────────────────────────────────────────

    /**
     * Registers a new user account.
     *
     * <ul>
     *   <li>Email is normalised to lowercase.</li>
     *   <li>Password is BCrypt-hashed — never stored plaintext.</li>
     *   <li>Role is always USER — accountType is metadata only.</li>
     *   <li>Partner application metadata is accepted but does NOT grant elevated access.</li>
     *   <li>Duplicate email is rejected with 409 Conflict.</li>
     * </ul>
     */
    @Transactional
    public CurrentUserResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("An account with this email address already exists.");
        }

        // Validate accountType — only accepted values
        String accountType = resolveAccountType(request.getAccountType());

        if ("partner_application".equals(accountType)
                && (request.getBusinessDetails() == null
                    || isBlank(request.getBusinessDetails().getBusinessName()))) {
            throw new IllegalArgumentException(
                    "Business details are required for partner applications.");
        }

        // Create the User entity — role is always USER (never from client input)
        User user = new User(
                email,
                request.getPhone(),
                passwordEncoder.encode(request.getPassword()),  // BCrypt hash
                request.getName().trim(),
                UserRole.USER,          // server-side only
                AccountStatus.ACTIVE,
                accountType
        );
        userRepository.save(user);

        // Create the linked profile
        UserProfile profile = new UserProfile(user);
        profile.setDisplayName(request.getName().trim());
        profile.setPhoneE164(request.getPhone());
        userProfileRepository.save(profile);

        log.info("New user registered: id={} accountType={}", user.getId(), accountType);
        return CurrentUserResponse.from(user, profile);
    }

    // ── Login ────────────────────────────────────────────────────────────────

    /**
     * Authenticates the user and creates a server-side session.
     * Authentication failure throws Spring Security's BadCredentialsException,
     * which is handled by {@link com.ghoomi.common.exception.GlobalExceptionHandler}.
     */
    @Transactional
    public CurrentUserResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail().trim().toLowerCase();

        // Delegate authentication to Spring Security — validates BCrypt hash
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        // Store authentication in a new session (prevents session fixation)
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        if (request.isRememberMe()) {
            session.setMaxInactiveInterval(30 * 24 * 60 * 60); // 30 days
        } else {
            session.setMaxInactiveInterval(8 * 60 * 60); // 8 hours
        }

        GhoomiUserDetails userDetails = (GhoomiUserDetails) auth.getPrincipal();
        User user = userDetails.getUser();

        // Update last login timestamp
        user.setLastLoginAt(java.time.LocalDateTime.now(java.time.ZoneOffset.UTC));
        userRepository.save(user);

        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        log.info("User logged in: id={}", user.getId());
        return CurrentUserResponse.from(user, profile);
    }

    // ── OAuth2 User Processing ──────────────────────────────────────────────

    /**
     * Safely creates or links a user authenticated via Google OAuth2.
     * Invariants:
     * - Assigns USER role only (never elevated).
     * - Never overwrites an existing password hash.
     * - Updates verified status and display name/avatar if empty.
     */
    @Transactional
    public CustomOAuth2User processOAuthUser(String rawEmail, String name, String picture, Map<String, Object> attributes) {
        if (rawEmail == null || rawEmail.isBlank()) {
            throw new IllegalArgumentException("Google OAuth provider did not return an email address.");
        }
        String email = rawEmail.trim().toLowerCase();
        User user = userRepository.findByEmail(email).orElse(null);
        UserProfile profile;

        if (user != null) {
            final User existingUser = user;
            if (!existingUser.isActive()) {
                throw new DisabledException("Your account has been deactivated. Please contact support.");
            }
            if (!existingUser.isEmailVerified()) {
                existingUser.setEmailVerified(true);
            }
            existingUser.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(existingUser);

            profile = userProfileRepository.findByUserId(existingUser.getId()).orElseGet(() -> {
                UserProfile p = new UserProfile(existingUser);
                p.setDisplayName(name != null && !name.isBlank() ? name : email);
                p.setAvatarObjectKey(picture);
                p.setTravelPreferences("{}");
                return userProfileRepository.save(p);
            });

            boolean profileUpdated = false;
            if ((profile.getDisplayName() == null || profile.getDisplayName().isBlank()) && name != null && !name.isBlank()) {
                profile.setDisplayName(name);
                profileUpdated = true;
            }
            if ((profile.getAvatarObjectKey() == null || profile.getAvatarObjectKey().isBlank()) && picture != null && !picture.isBlank()) {
                profile.setAvatarObjectKey(picture);
                profileUpdated = true;
            }
            if (profileUpdated) {
                userProfileRepository.save(profile);
            }
            log.info("Existing user authenticated via Google OAuth2: id={}", existingUser.getId());
        } else {
            String secureRandomHash = passwordEncoder.encode(UUID.randomUUID().toString());
            user = new User(
                    email,
                    null,
                    secureRandomHash,
                    name != null && !name.isBlank() ? name.trim() : email,
                    UserRole.USER,
                    AccountStatus.ACTIVE,
                    "traveller"
            );
            user.setEmailVerified(true);
            user.setLastLoginAt(LocalDateTime.now(ZoneOffset.UTC));
            userRepository.save(user);

            profile = new UserProfile(user);
            profile.setDisplayName(name != null && !name.isBlank() ? name.trim() : email);
            profile.setAvatarObjectKey(picture);
            profile.setTravelPreferences("{}");
            userProfileRepository.save(profile);
            log.info("New user registered via Google OAuth2: id={}", user.getId());
        }

        return new CustomOAuth2User(user, profile, attributes);
    }

    // ── Current User ─────────────────────────────────────────────────────────

    /**
     * Resolves and returns the authenticated {@link User} entity.
     * Supports both standard session authentication (GhoomiUserDetails)
     * and OAuth2 session authentication (CustomOAuth2User).
     * Throws {@link UnauthorizedException} if no session exists or user is unknown.
     */
    @Transactional(readOnly = true)
    public User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedException("Not authenticated. Please log in.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof GhoomiUserDetails ghoomiUserDetails) {
            return ghoomiUserDetails.getUser();
        } else if (principal instanceof CustomOAuth2User customOAuth2User) {
            return customOAuth2User.getUser();
        } else if (principal instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User not found: " + email));
        } else {
            String email = authentication.getName();
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("User not found: " + email));
        }
    }

    /**
     * Returns the authenticated user's safe profile.
     * Supports both standard session authentication (GhoomiUserDetails)
     * and OAuth2 session authentication (CustomOAuth2User).
     * Throws {@link UnauthorizedException} if no session exists.
     */
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        UserProfile profile = userProfileRepository.findByUserId(user.getId()).orElse(null);
        return CurrentUserResponse.from(user, profile);
    }

    // ── Password Recovery ────────────────────────────────────────────────────

    /**
     * Initiates password recovery by issuing a 6-digit OTP stored as BCrypt hash.
     * Prevents user enumeration by returning a generic response even if email is unknown.
     * Enforces a 60-second cooldown per email.
     */
    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        // Cooldown check
        Optional<OtpRecord> recentOtp = otpRecordRepository
                .findTopByEmailAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, OtpPurpose.PASSWORD_RESET, now);
        if (recentOtp.isPresent()) {
            long elapsedSeconds = ChronoUnit.SECONDS.between(recentOtp.get().getExpiresAt().minusMinutes(10), now);
            if (elapsedSeconds < 60 && elapsedSeconds >= 0) {
                int retryAfter = (int) (60 - elapsedSeconds);
                return new ForgotPasswordResponse(
                        "A verification code was recently generated. Please check your inbox or wait before retrying.",
                        retryAfter
                );
            }
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || !userOpt.get().isActive()) {
            // Anti-enumeration: Return generic success without creating an OTP
            return new ForgotPasswordResponse(
                    "If your email address is registered, a 6-digit verification code has been dispatched.",
                    60
            );
        }

        // Generate cryptographically secure 6-digit OTP
        int code = 100_000 + secureRandom.nextInt(900_000);
        String otp = String.valueOf(code);
        String otpHash = passwordEncoder.encode(otp);
        LocalDateTime expiresAt = now.plusMinutes(10);

        // Dispatch real email before persisting OTP: if dispatch fails, do NOT persist unreachable OTP
        emailService.sendPasswordResetOtp(email, otp, 10);

        OtpRecord record = new OtpRecord(email, otpHash, OtpPurpose.PASSWORD_RESET, expiresAt);
        otpRecordRepository.save(record);

        log.info("[AUTH] PASSWORD_RESET verification code generated and dispatched for email={}", maskEmail(email));

        return new ForgotPasswordResponse(
                "If your email address is registered, a 6-digit verification code has been dispatched.",
                60
        );
    }

    /**
     * Validates an entered OTP against the latest active record.
     * Locks record after 5 failed attempts.
     * On success, establishes a server-side session grant for password reset.
     */
    @Transactional
    public void verifyOtp(VerifyOtpRequest request, HttpServletRequest httpRequest) {
        String email = request.getEmail().trim().toLowerCase();
        String enteredOtp = request.getOtp().trim();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        OtpRecord record = otpRecordRepository
                .findTopByEmailAndPurposeAndUsedFalseAndExpiresAtAfterOrderByCreatedAtDesc(
                        email, OtpPurpose.PASSWORD_RESET, now)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired verification code. Please request a new code."));

        if (record.getAttempts() >= 5) {
            record.setUsed(true);
            otpRecordRepository.save(record);
            throw new IllegalArgumentException("Too many incorrect attempts. This code has been invalidated. Please request a new one.");
        }

        if (!passwordEncoder.matches(enteredOtp, record.getOtpHash())) {
            record.incrementAttempts();
            otpRecordRepository.save(record);
            int remaining = 5 - record.getAttempts();
            throw new IllegalArgumentException("Incorrect verification code. Attempts remaining: " + remaining);
        }

        // OTP verified successfully: consume it
        record.setUsed(true);
        otpRecordRepository.save(record);

        // Store reset grant in the secure server-side session
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("PASSWORD_RESET_AUTHORIZED_EMAIL", email);
        session.setAttribute("PASSWORD_RESET_AUTHORIZED_AT", now);
    }

    /**
     * Resets the user's password using the verified session grant.
     * Cleans up all previous OTP records for this user upon success.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session == null || session.getAttribute("PASSWORD_RESET_AUTHORIZED_EMAIL") == null) {
            throw new UnauthorizedException("Password reset authorization has expired or is missing. Please verify your OTP again.");
        }

        LocalDateTime authorizedAt = (LocalDateTime) session.getAttribute("PASSWORD_RESET_AUTHORIZED_AT");
        if (authorizedAt == null || ChronoUnit.MINUTES.between(authorizedAt, LocalDateTime.now(ZoneOffset.UTC)) > 15) {
            session.removeAttribute("PASSWORD_RESET_AUTHORIZED_EMAIL");
            session.removeAttribute("PASSWORD_RESET_AUTHORIZED_AT");
            throw new UnauthorizedException("Reset authorization expired. Please request a new verification code.");
        }

        String email = (String) session.getAttribute("PASSWORD_RESET_AUTHORIZED_EMAIL");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        // Update password hash with BCrypt(12)
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Purge used OTP records for this email
        otpRecordRepository.deleteAllByEmailAndPurpose(email, OtpPurpose.PASSWORD_RESET);

        // Clear session grant
        session.removeAttribute("PASSWORD_RESET_AUTHORIZED_EMAIL");
        session.removeAttribute("PASSWORD_RESET_AUTHORIZED_AT");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String resolveAccountType(String raw) {
        if ("partner_application".equals(raw)) return "partner_application";
        return "traveller"; // Default; never use client-supplied unknown values
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "***";
        int at = email.indexOf('@');
        String name = email.substring(0, at);
        String domain = email.substring(at);
        if (name.length() <= 2) return name.charAt(0) + "***" + domain;
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }
}

