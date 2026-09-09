package com.antony.openjobs.messaging.consumers;

import com.antony.openjobs.services.email.EmailMessage;
import com.antony.openjobs.services.email.EmailService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailConsumerTest {

    @Test
    void shouldSendEmailWhenAMessageIsConsumed() {
        EmailService emailService = mock(EmailService.class);
        EmailConsumer consumer = new EmailConsumer(emailService);
        EmailMessage message = new EmailMessage(
                "candidate@example.com",
                "Bem-vindo à OpenJobs!",
                "Sua conta foi criada com sucesso."
        );

        consumer.consume(message);

        verify(emailService).sendEmail(message);
    }
}
