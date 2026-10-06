package com.antony.openjobs.config.security;

import com.antony.openjobs.modules.rolepermissions.repository.IRolePermissionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityErrorResponseTest {

    private AnnotationConfigWebApplicationContext context;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigWebApplicationContext();
        context.setServletContext(new MockServletContext());
        TestPropertySourceUtils.addInlinedPropertiesToEnvironment(context,
                "jwt.expiration=60000", "jwt.key=test-key");
        context.register(TestConfiguration.class);
        context.refresh();
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilters(new DelegatingFilterProxy("springSecurityFilterChain", context))
                .build();
    }

    @AfterEach
    void tearDown() {
        context.close();
    }

    @Test
    void returnsJsonInsteadOfHtmlWhenAuthenticationIsMissing() throws Exception {
        mvc.perform(get("/protected").header(HttpHeaders.ACCEPT, "text/html"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.errors[0].field").isEmpty())
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Autenticação necessária ou token inválido"));
    }

    @Test
    void returnsUnauthorizedForInvalidToken() throws Exception {
        mvc.perform(get("/protected").header(HttpHeaders.AUTHORIZATION, "Bearer invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void returnsJsonForAuthenticatedUserWithoutPermission() throws Exception {
        TokenProvider provider = context.getBean(TokenProvider.class);
        when(provider.isTokenValid("valid")).thenReturn(true);
        when(provider.getAuthenticatedUser("valid"))
                .thenReturn(new AuthenticatedUser(UUID.randomUUID(), UUID.randomUUID()));

        mvc.perform(get("/denied").header(HttpHeaders.AUTHORIZATION, "Bearer valid"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Você não tem permissão para esta ação"));
    }

    @Test
    void returnsJsonForDisallowedCorsOrigin() throws Exception {
        mvc.perform(options("/protected")
                        .header(HttpHeaders.ORIGIN, "https://example.com")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET"))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Origem, método ou cabeçalhos não permitidos pelo CORS"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void preservesAllowedPreflightAndCorsHeadersOnAuthenticationErrors() throws Exception {
        mvc.perform(options("/protected")
                        .header(HttpHeaders.ORIGIN, "https://web.openjobs.shop")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "GET")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://web.openjobs.shop"));

        mvc.perform(get("/protected").header(HttpHeaders.ORIGIN, "https://web.openjobs.shop"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
                        "https://web.openjobs.shop"))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void preservesPublicEndpointWithoutOrigin() throws Exception {
        mvc.perform(get("/actuator/health/readiness"))
                .andExpect(status().isOk())
                .andExpect(content().string("UP"));
    }

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import({SecurityConfiguration.class, TestController.class})
    static class TestConfiguration {
        @Bean
        TokenProvider tokenProvider() {
            return mock(TokenProvider.class);
        }

        @Bean
        JwtAuthenticationFilter jwtAuthenticationFilter(TokenProvider provider) {
            return new JwtAuthenticationFilter(provider);
        }

        @Bean
        SecurityErrorHandler securityErrorHandler() {
            return new SecurityErrorHandler(JsonMapper.builder().build());
        }

        @Bean
        RequiresPermissionAuthorizationManager permissionManager() {
            return new RequiresPermissionAuthorizationManager(mock(IRolePermissionRepository.class));
        }
    }

    @RestController
    static class TestController {
        @GetMapping("/protected")
        String protectedEndpoint() {
            return "OK";
        }

        @GetMapping("/denied")
        @PreAuthorize("denyAll()")
        public String denied() {
            return "OK";
        }

        @GetMapping("/actuator/health/readiness")
        String health() {
            return "UP";
        }
    }
}
