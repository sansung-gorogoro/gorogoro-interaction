package com.example.lxp.common.messaging.adapter.out.rabbit;

import com.example.lxp.common.messaging.config.MessagingProps;
import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.common.messaging.domain.model.EventEnvelope;
import com.example.lxp.common.messaging.exception.EventPublishException;
import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class RabbitEventPublisherAdapter implements EventPublisherPort {

    private final RabbitTemplate template;
    private final MessagingProps props;

    public RabbitEventPublisherAdapter(RabbitTemplate template, MessagingProps props) {
        this.template = template;
        this.props = props;
    }

    @Override
    public void publish(DomainEvent event) {
        EventEnvelope<DomainEvent> envelope = EventEnvelope.wrap(event);
        String exchange = requireNonBlank(props.exchange(), "rabbit events exchange");
        String routingKey = requireNonBlank(props.toRoutingKey(envelope.type()), "rabbit events routingKey");
        CorrelationData correlation = new CorrelationData(envelope.eventId().toString());

        try {
            template.convertAndSend(
                    exchange,
                    routingKey,
                    envelope.payload(),
                    msg -> {
                        MessageProperties mp = msg.getMessageProperties();
                        mp.setMessageId(envelope.eventId().toString());
                        mp.setTimestamp(Date.from(envelope.occurredAt()));
                        mp.setType(envelope.type());
                        mp.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                        mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                        mp.setHeader("version", envelope.version());
                        envelope.metadata().forEach((k, v) -> mp.setHeader("meta-" + k, v));
                        return msg;
                    },
                    correlation
            );
        } catch (AmqpException ex) {
            throw new EventPublishException(event.toString(), ex);
        }
    }

    private String requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " is not configured");
        }
        return value;
    }

}
