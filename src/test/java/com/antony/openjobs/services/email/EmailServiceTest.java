package com.antony.openjobs.services.email;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Test
    void shouldMapQueueMessageToEmailAndSendIt() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService emailService = new EmailService(mailSender);
        EmailMessage emailMessage = new EmailMessage(
                "candidate@example.com",
                "Bem-vindo à OpenJobs!",
                "Sua conta foi criada com sucesso."
        );

        emailService.sendEmail(emailMessage);

        ArgumentCaptor<SimpleMailMessage> mailCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(mailCaptor.capture());

        SimpleMailMessage sentMail = mailCaptor.getValue();
        assertThat(sentMail.getTo()).containsExactly("candidate@example.com");
        assertThat(sentMail.getSubject()).isEqualTo("Bem-vindo à OpenJobs!");
        assertThat(sentMail.getText()).isEqualTo("Sua conta foi criada com sucesso.");
    }
}
