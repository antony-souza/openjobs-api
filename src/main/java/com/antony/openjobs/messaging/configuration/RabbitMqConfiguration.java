package com.antony.openjobs.config.queue;

import com.antony.openjobs.utils.RabbitQueues;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public Queue genericEmailQueue() {
        return new Queue(RabbitQueues.GENERIC_EMAILS, true);
    }
}
