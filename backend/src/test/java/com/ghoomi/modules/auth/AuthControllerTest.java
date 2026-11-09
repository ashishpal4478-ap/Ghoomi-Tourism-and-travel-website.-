package com.ghoomi.modules.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ghoomi.modules.auth.dto.*;
import com.ghoomi.modules.user.User;
import com.ghoomi.modules.user.UserRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpRecordRepository otpRecordRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    private static final String TEST_EMAIL = "authtest_runner@example.com";
    private static final String TEST_PASSWORD = "StrongPassword123!";

    @BeforeEach
    void setup() {
        otpRecordRepository.deleteAllByEmailAndPurpose(TEST_EMAIL, OtpPurpose.PASSWORD_RESET);
        userRepository.findByEmail(TEST_EMAIL).ifPresent(userRepository::delete);

        Session session = Session.getInstance(new Properties());
        Mockito.when(mailSender.createMimeMessage()).thenReturn(new MimeMessage(session));
    }

    @Test
    void csrfEndpointReturnsOk() throws Exception {
        mockMvc.perform(get("/api/v1/auth/csrf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void unauthenticatedAccessToMeReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationAndLoginLifecycle() throws Exception {
        // 1. Register new user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Lifecycle User");
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPhone("+919876543210");
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setAccountType("traveller");

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.data.role").value("USER"));

        // Verify password is encrypted in database
        User savedUser = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.getPasswordHash()));

        // 2. Reject duplicate email registration with 409 Conflict
        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));

        // 3. Reject login with wrong password
        LoginRequest badLogin = new LoginRequest();
        badLogin.setEmail(TEST_EMAIL);
        badLogin.setPassword("WrongPassword!");

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badLogin)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));

        // 4. Login with correct password and capture session
        LoginRequest goodLogin = new LoginRequest();
        goodLogin.setEmail(TEST_EMAIL);
        goodLogin.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL))
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        assertNotNull(session);

        // 5. Access /api/v1/auth/me with the authenticated session
        mockMvc.perform(get("/api/v1/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL));

        // 6. Logout
        mockMvc.perform(post("/api/v1/auth/logout")
                        .with(csrf())
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 7. Verify session is invalidated
        assertTrue(session.isInvalid());
    }

    @Test
    void passwordRecoveryLifecycle() throws Exception {
        // 1. Create user
        RegisterRequest register = new RegisterRequest();
        register.setName("Recovery User");
        register.setEmail(TEST_EMAIL);
        register.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 2. Request OTP via /password/forgot
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest(TEST_EMAIL);
        mockMvc.perform(post("/api/v1/auth/password/forgot")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 3. For test determinism, set known OTP record "654321"
        otpRecordRepository.deleteAllByEmailAndPurpose(TEST_EMAIL, OtpPurpose.PASSWORD_RESET);
        OtpRecord deterministicOtp = new OtpRecord(
                TEST_EMAIL,
                passwordEncoder.encode("654321"),
                OtpPurpose.PASSWORD_RESET,
                LocalDateTime.now(ZoneOffset.UTC).plusMinutes(10)
        );
        otpRecordRepository.save(deterministicOtp);

        // 4. Bad OTP attempt should fail
        VerifyOtpRequest badOtp = new VerifyOtpRequest(TEST_EMAIL, "000000");
        mockMvc.perform(post("/api/v1/auth/password/verify-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badOtp)))
                .andExpect(status().isBadRequest());

        // 5. Valid OTP attempt succeeds and establishes reset grant session
        VerifyOtpRequest goodOtp = new VerifyOtpRequest(TEST_EMAIL, "654321");
        MvcResult verifyResult = mockMvc.perform(post("/api/v1/auth/password/verify-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(goodOtp)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        MockHttpSession resetSession = (MockHttpSession) verifyResult.getRequest().getSession(false);
        assertNotNull(resetSession);

        // 6. Reset password using the verified session
        String newPassword = "BrandNewPassword123#";
        ResetPasswordRequest resetReq = new ResetPasswordRequest(newPassword);
        mockMvc.perform(post("/api/v1/auth/password/reset")
                        .with(csrf())
                        .session(resetSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 7. Old password no longer works
        LoginRequest oldLogin = new LoginRequest();
        oldLogin.setEmail(TEST_EMAIL);
        oldLogin.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldLogin)))
                .andExpect(status().isUnauthorized());

        // 8. New password works
        LoginRequest newLogin = new LoginRequest();
        newLogin.setEmail(TEST_EMAIL);
        newLogin.setPassword(newPassword);

        mockMvc.perform(post("/api/v1/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newLogin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(TEST_EMAIL));

        // 9. Verify OTP email delivery was invoked
        verify(mailSender, atLeastOnce()).send(any(MimeMessage.class));
    }

    @Test
    void forgotPassword_smtpFailure_doesNotPersistOtpAndReturns503() throws Exception {
        // Register user
        RegisterRequest register = new RegisterRequest();
        register.setName("SMTP Fail User");
        register.setEmail(TEST_EMAIL);
        register.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // Configure JavaMailSender to simulate SMTP connection timeout
        doThrow(new MailSendException("SMTP server connection timeout: smtp.gmail.com:587"))
                .when(mailSender).send(any(MimeMessage.class));

        // Request forgot-password
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest(TEST_EMAIL);
        mockMvc.perform(post("/api/v1/auth/password/forgot")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Unable to send verification email. Please check your connection or try again shortly."));

        // Crucial security invariant: no OTP record should be saved in DB when delivery fails!
        assertEquals(0, otpRecordRepository.countByEmailAndPurpose(TEST_EMAIL, OtpPurpose.PASSWORD_RESET),
                "No OTP record should be saved when outbound email dispatch fails");
    }

    @Test
    void forgotPassword_antiEnumeration_unknownUser_doesNotInvokeMailSender() throws Exception {
        String nonExistentEmail = "completely_unknown_user_12345@example.com";

        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest(nonExistentEmail);
        mockMvc.perform(post("/api/v1/auth/password/forgot")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("If your email address is registered, a 6-digit verification code has been dispatched."));

        // Must never invoke mailSender for an unregistered address
        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void forgotPassword_cooldownPreserved_preventsSpam() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setName("Cooldown User");
        register.setEmail(TEST_EMAIL);
        register.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        // 1st request -> dispatches email
        ForgotPasswordRequest forgotReq = new ForgotPasswordRequest(TEST_EMAIL);
        mockMvc.perform(post("/api/v1/auth/password/forgot")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(mailSender, times(1)).send(any(MimeMessage.class));

        // 2nd request immediately -> within 60-second cooldown
        mockMvc.perform(post("/api/v1/auth/password/forgot")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.retryAfterSeconds").isNumber());

        // mailSender must still only have been called once!
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}

