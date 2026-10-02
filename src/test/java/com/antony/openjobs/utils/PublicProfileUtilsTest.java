package com.antony.openjobs.utils;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PublicProfileUtilsTest {
    @Test
    void rejectsUnsafeOrMalformedPortfolioLinksAndAcceptsHttpUrls() {
        assertThat(PublicProfileUtils.cleanUrl("  https://example.com/portfolio  ")).isEqualTo("https://example.com/portfolio");
        assertThat(PublicProfileUtils.cleanUrl("HTTPS://example.com")).isEqualTo("HTTPS://example.com");
        assertThat(PublicProfileUtils.cleanUrl("")).isNull();
        for (var value : List.of("javascript:alert(1)", "//example.com", "meusite", "https://", "https://user:password@example.com", "https://example.com/a b")) {
            assertThatThrownBy(() -> PublicProfileUtils.cleanUrl(value)).isInstanceOf(ResponseStatusException.class);
        }
    }
}
