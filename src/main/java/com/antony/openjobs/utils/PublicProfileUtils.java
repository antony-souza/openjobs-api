package com.antony.openjobs.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;

public final class PublicProfileUtils {
    private PublicProfileUtils() {}

    public static String cleanText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public static String cleanUrl(String value) {
        var normalized = cleanText(value);
        if (normalized == null) return null;
        try {
            var uri = URI.create(normalized);
            if (("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme())) && uri.getHost() != null && uri.getUserInfo() == null) {
                return normalized;
            }
        } catch (IllegalArgumentException ignored) {
            // Report malformed URLs using the same validation message.
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um link válido começando com https:// ou http://");
    }
}
