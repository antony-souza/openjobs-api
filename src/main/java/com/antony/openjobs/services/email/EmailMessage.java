package com.antony.openjobs.services.email;

import java.io.Serializable;

public record EmailMessageDto(
        String to,
        String subject,
        String body
) implements Serializable {
}
