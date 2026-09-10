package com.antony.openjobs.services.email;

import java.io.Serializable;

public record EmailMessage(
        String to,
        String subject,
        String body
){}
