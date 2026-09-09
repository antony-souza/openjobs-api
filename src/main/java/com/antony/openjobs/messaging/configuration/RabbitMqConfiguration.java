package com.antony.openjobs.messaging.configuration;

import com.antony.openjobs.utils.QueueNameUtils;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfiguration {

    @Bean
    public Queue genericEmailQueue() {
        return new Queue(QueueNameUtils.GENERIC_EMAILS, true);
    }
}
