package com.ghoomi.common.config;

import com.ghoomi.modules.auth.GhoomiUserDetailsService;
import com.ghoomi.modules.auth.oauth2.CustomOAuth2UserService;
import com.ghoomi.modules.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.ghoomi.modules.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security configuration.
 *
 * <p>Session-based authentication (HTTP session, not JWT).
 * CSRF protection is enabled using a cookie-based token readable by the frontend.
 * Supports both standard email/password login and Google OAuth2 login.
 * Credentials must be included in all cross-origin fetch requests.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    private final GhoomiUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(GhoomiUserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Lazy CustomOAuth2UserService customOAuth2UserService,
            @Lazy OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler,
            @Lazy OAuth2AuthenticationFailureHandler oAuth2FailureHandler) throws Exception {
        // CSRF: use cookie-based token so the frontend can read XSRF-TOKEN and
        // send it back as X-XSRF-TOKEN header on mutating requests.
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        // Spring Security 6 requires explicit handler to read the token from request attributes
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null); // defer token loading

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(csrfHandler)
                // Public safe endpoints do not need CSRF
                .ignoringRequestMatchers(
                    "/api/v1/health",
                    "/api/v1/auth/csrf",
                    "/oauth2/**",
                    "/login/oauth2/**"
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(5)                      // allow multiple devices
                .expiredSessionStrategy(event ->
                    event.getResponse().setStatus(HttpStatus.UNAUTHORIZED.value()))
            )
            .authenticationProvider(daoAuthenticationProvider())
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
                .redirectionEndpoint(redir -> redir.baseUri("/login/oauth2/code/*"))
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                .successHandler(oAuth2SuccessHandler)
                .failureHandler(oAuth2FailureHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Health and CSRF init — always public
                .requestMatchers("/api/v1/health").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/auth/csrf").permitAll()
                // OAuth2 flow endpoints
                .requestMatchers(
                    "/oauth2/**",
                    "/login/oauth2/**"
                ).permitAll()
                // Destination and catalog browsing — public read-only
                .requestMatchers(HttpMethod.GET,
                    "/api/v1/destinations/**",
                    "/api/v1/places/**"
                ).permitAll()
                // Auth endpoints — registration, login, password recovery
                .requestMatchers(HttpMethod.POST,
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/logout",
                    "/api/v1/auth/password/forgot",
                    "/api/v1/auth/password/verify-otp",
                    "/api/v1/auth/password/reset"
                ).permitAll()
                // Everything else requires authentication
                .anyRequest().authenticated()
            )
            // Return 401 JSON (not 302 redirect) for unauthenticated API calls
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.stream(allowedOrigins.split(",")).map(String::trim).toList());
        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-Requested-With",
                "Accept", "Origin", "X-XSRF-TOKEN",
                "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(List.of("XSRF-TOKEN"));
        configuration.setAllowCredentials(true); // Required for session cookies
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

