package com.example.lxp.common.adapter.out;

import com.example.lxp.common.port.out.EventPublisherPort;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherAdapter implements EventPublisherPort {

    @Override
    public void publish(Object event) {
        // TODO: Implement actual event publishing to RabbitMQ (or other external message broker)
    }

}
