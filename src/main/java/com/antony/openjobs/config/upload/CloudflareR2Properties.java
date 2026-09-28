package com.antony.openjobs.config.upload;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "cloudflare.r2")
public record CloudflareR2Properties(
        boolean enabled,
        @NotBlank String accountId,
        @NotBlank String accessKeyId,
        @NotBlank String secretAccessKey,
        @NotBlank String bucketName,
        @NotBlank String publicUrl
) {
    public String endpoint() {
        return "https://%s.r2.cloudflarestorage.com".formatted(accountId);
    }
}
