package com.example.lxp.common.messaging.port.out;

import com.example.lxp.common.messaging.domain.model.DomainEvent;

public interface EventPublisherPort {

    void publish(DomainEvent event);

}
