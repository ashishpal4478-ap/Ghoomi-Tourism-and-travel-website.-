package com.ghoomi.modules.health;

import com.ghoomi.common.config.PasswordEncoderConfig;
import com.ghoomi.common.config.SecurityConfig;
import com.ghoomi.modules.auth.GhoomiUserDetailsService;
import com.ghoomi.modules.auth.oauth2.CustomOAuth2UserService;
import com.ghoomi.modules.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.ghoomi.modules.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import({SecurityConfig.class, PasswordEncoderConfig.class})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GhoomiUserDetailsService ghoomiUserDetailsService;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @MockBean
    private OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;

    @MockBean
    private OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    @Test
    void healthCheckShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Ghoomi backend is operational"))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("ghoomi-backend"));
    }
}
