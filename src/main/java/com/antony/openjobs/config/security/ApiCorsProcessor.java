package com.antony.openjobs.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.cors.DefaultCorsProcessor;

import java.io.IOException;

@RequiredArgsConstructor
public class ApiCorsProcessor extends DefaultCorsProcessor {

    private final SecurityErrorHandler securityErrorHandler;

    @Override
    protected void rejectRequest(ServerHttpResponse response) throws IOException {
        securityErrorHandler.write(response, HttpStatus.FORBIDDEN,
                "Origem, método ou cabeçalhos não permitidos pelo CORS");
    }
}
