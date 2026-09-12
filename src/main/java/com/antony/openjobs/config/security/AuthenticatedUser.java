package com.antony.openjobs.config.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId
) {
}
