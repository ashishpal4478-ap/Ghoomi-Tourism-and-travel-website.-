package com.ghoomi.modules.auth.oauth2;

import com.ghoomi.modules.auth.AuthService;
import com.ghoomi.modules.user.AccountStatus;
import com.ghoomi.modules.user.User;
import com.ghoomi.modules.user.UserRepository;
import com.ghoomi.modules.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.DisabledException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomOAuth2UserServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    private static final String GOOGLE_EMAIL = "google_test_user@example.com";

    @BeforeEach
    void setup() {
        userRepository.findByEmail(GOOGLE_EMAIL).ifPresent(userRepository::delete);
    }

    @Test
    void processOAuthUserCreatesNewUserWithUserRole() {
        Map<String, Object> attributes = Map.of(
                "sub", "google-sub-12345",
                "email", GOOGLE_EMAIL,
                "name", "Google Traveler",
                "picture", "https://example.com/avatar.jpg"
        );

        CustomOAuth2User oauthUser = authService.processOAuthUser(
                GOOGLE_EMAIL,
                "Google Traveler",
                "https://example.com/avatar.jpg",
                attributes
        );

        assertNotNull(oauthUser);
        assertEquals(GOOGLE_EMAIL, oauthUser.getName());

        User user = userRepository.findByEmail(GOOGLE_EMAIL).orElse(null);
        assertNotNull(user);
        assertEquals(UserRole.USER, user.getRole(), "New OAuth users must strictly receive USER role");
        assertTrue(user.isEmailVerified(), "Google-authenticated emails are marked verified");
        assertNotNull(user.getPasswordHash(), "A secure non-usable password hash must be generated");

        assertNotNull(oauthUser.getProfile());
        assertEquals("Google Traveler", oauthUser.getProfile().getDisplayName());
        assertEquals("https://example.com/avatar.jpg", oauthUser.getProfile().getAvatarObjectKey());
    }

    @Test
    void processOAuthUserLinksExistingUserWithoutOverwritingPassword() {
        // Pre-create user with a known password hash
        String originalHash = "$2a$12$OriginalHashNotOverwrittenByOAuthLoginTest1234567890";
        User existing = new User(
                GOOGLE_EMAIL,
                "+919876543210",
                originalHash,
                "Existing Traveler",
                UserRole.USER,
                AccountStatus.ACTIVE,
                "traveller"
        );
        userRepository.save(existing);

        Map<String, Object> attributes = Map.of(
                "sub", "google-sub-67890",
                "email", GOOGLE_EMAIL,
                "name", "Google Name Update",
                "picture", "https://example.com/new_pic.jpg"
        );

        CustomOAuth2User oauthUser = authService.processOAuthUser(
                GOOGLE_EMAIL,
                "Google Name Update",
                "https://example.com/new_pic.jpg",
                attributes
        );

        assertNotNull(oauthUser);

        User reloaded = userRepository.findByEmail(GOOGLE_EMAIL).orElseThrow();
        assertEquals(originalHash, reloaded.getPasswordHash(), "Existing password hash must never be overwritten");
        assertTrue(reloaded.isEmailVerified(), "Email verified flag should be updated");
    }

    @Test
    void processOAuthUserRejectsDeactivatedAccount() {
        User deactivated = new User(
                GOOGLE_EMAIL,
                "+919876543210",
                "hash",
                "Deactivated Traveler",
                UserRole.USER,
                AccountStatus.DEACTIVATED,
                "traveller"
        );
        deactivated.setActive(false);
        userRepository.save(deactivated);

        Map<String, Object> attributes = Map.of(
                "sub", "google-sub-99999",
                "email", GOOGLE_EMAIL
        );

        assertThrows(DisabledException.class, () ->
                authService.processOAuthUser(GOOGLE_EMAIL, "Deactivated", null, attributes)
        );
    }
}
