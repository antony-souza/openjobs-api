package com.antony.openjobs.messaging.consumers;

import com.antony.openjobs.services.email.EmailMessage;
import com.antony.openjobs.services.email.EmailService;
import com.antony.openjobs.utils.QueueNameUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {
    private final EmailService emailService;

    @RabbitListener(queues = QueueNameUtils.GENERIC_EMAILS)
    public void consume(EmailMessage message) {
        emailService.sendEmail(message);
    }
}
