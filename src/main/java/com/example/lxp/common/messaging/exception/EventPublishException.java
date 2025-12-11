package com.example.lxp.common.messaging.exception;

import org.springframework.amqp.AmqpException;

public class EventPublishException extends AmqpException {

    public EventPublishException(String message) {
        super(message);
    }

    public EventPublishException(Throwable cause) {
        super(cause);
    }

    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }

}
