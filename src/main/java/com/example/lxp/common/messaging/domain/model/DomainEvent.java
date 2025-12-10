package com.example.lxp.common.messaging.domain.model;

public interface DomainEvent {

    String type();

    default String version() {
        return "v1";
    }

}
