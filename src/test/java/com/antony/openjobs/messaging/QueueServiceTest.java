package com.antony.openjobs.messaging;

import com.antony.openjobs.services.email.EmailMessage;
import com.antony.openjobs.services.queue.QueueService;
import com.antony.openjobs.utils.QueueNameUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QueueServiceTest {

    @Test
    void shouldAddMessageToTheSpecifiedQueue() {
        RabbitTemplate rabbitTemplate = mock(RabbitTemplate.class);
        QueueService queueService = new QueueService(rabbitTemplate);
        EmailMessage message = new EmailMessage(
                "candidate@example.com",
                "Bem-vindo à OpenJobs!",
                "Sua conta foi criada com sucesso."
        );

        queueService.addInQueue(QueueNameUtils.GENERIC_EMAILS, message);

        verify(rabbitTemplate).convertAndSend(QueueNameUtils.GENERIC_EMAILS, message);
    }
}
