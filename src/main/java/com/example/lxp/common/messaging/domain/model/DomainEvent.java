package com.example.lxp.common.messaging.domain.model;

public interface DomainEvent {

    String getServicePrefix();

    String getEventName();

}
