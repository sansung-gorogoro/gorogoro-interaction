package com.example.lxp.common.messaging.port.out;

import com.example.lxp.common.messaging.domain.model.DomainEvent;

public interface EventPublisherPort {

    <T extends DomainEvent> void publish(T payload);

    <T extends DomainEvent> void publish(T payload, String traceId);

}
