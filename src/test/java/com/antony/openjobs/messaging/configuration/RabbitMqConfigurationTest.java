package com.antony.openjobs.messaging.configuration;

import com.antony.openjobs.utils.QueueNameUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitMqConfigurationTest {

    @Test
    void shouldDeclareDurableGenericEmailQueue() {
        RabbitMqConfiguration configuration = new RabbitMqConfiguration();

        var queue = configuration.genericEmailQueue();

        assertThat(queue.getName()).isEqualTo(QueueNameUtils.GENERIC_EMAILS);
        assertThat(queue.isDurable()).isTrue();
    }
}
