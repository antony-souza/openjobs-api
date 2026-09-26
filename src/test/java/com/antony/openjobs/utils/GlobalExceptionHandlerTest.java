package com.antony.openjobs.utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    @Test
    void returnsForbiddenWhenPermissionIsDenied() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new DeniedController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mvc.perform(get("/denied"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0].message")
                        .value("Você não tem permissão para esta ação"));
    }

    @RestController
    static class DeniedController {
        @GetMapping("/denied")
        void denied() {
            throw new AccessDeniedException("Acesso negado");
        }
    }
}
