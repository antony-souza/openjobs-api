package com.antony.openjobs.services.queue;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final RabbitTemplate rabbitTemplate;

    public void addInQueue(String queueName, Object message) {
        rabbitTemplate.convertAndSend(queueName, message);
    }
}
