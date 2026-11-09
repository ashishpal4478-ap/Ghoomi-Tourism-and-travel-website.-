package com.ghoomi.modules.auth.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Handles failed OAuth2 authentication with Google.
 * Redirects back to the frontend login page with an error parameter.
 */
@Component
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationFailureHandler.class);

    @Value("${app.oauth2.authorized-redirect-uri:http://localhost:3000/login}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        log.warn("Google OAuth2 authentication failed: {}", exception.getMessage());

        String targetUrl = redirectUri;
        String errorMessage = exception.getMessage() != null ? exception.getMessage() : "Authentication failed";
        String encodedError = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        targetUrl += (targetUrl.contains("?") ? "&" : "?") + "oauth=error&message=" + encodedError;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}

