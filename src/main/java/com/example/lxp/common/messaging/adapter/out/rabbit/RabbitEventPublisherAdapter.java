package com.example.lxp.common.messaging.adapter.out.rabbit;

import com.example.lxp.common.messaging.config.MessagingProps;
import com.example.lxp.common.messaging.domain.model.DomainEvent;
import com.example.lxp.common.messaging.domain.model.EventEnvelope;
import com.example.lxp.common.messaging.domain.model.EventMetadata;
import com.example.lxp.common.messaging.domain.model.EventPriority;
import com.example.lxp.common.messaging.domain.model.EventSensitivity;
import com.example.lxp.common.messaging.port.out.EventPublisherPort;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class RabbitEventPublisherAdapter implements EventPublisherPort {

    public static final String CONTENT_ENCODING = "utf-8";

    private final RabbitTemplate template;
    private final MessagingProps props;

    public RabbitEventPublisherAdapter(RabbitTemplate template, MessagingProps props) {
        this.template = template;
        this.props = props;
    }

    @Override
    public <T extends DomainEvent> void publish(T payload) {
        publish(payload, null);
    }

    @Override
    public <T extends DomainEvent> void publish(T payload, String traceId) {
        String prefix = payload.getServicePrefix();
        String eventName = payload.getEventName();

        MessagingProps.Service service = props.services().get(prefix);
        Assert.notNull(service, "Unknown service key: " + prefix);
        Assert.hasText(eventName, "Event type must not be blank");

        String routingKey = service.prefix() + "." + eventName + ".event";
        String messageId = UUID.randomUUID().toString();
        String resolvedTraceId = StringUtils.hasText(traceId) ? traceId : UUID.randomUUID().toString();
        Instant now = Instant.now();

        MessagePostProcessor headers = buildHeaders(messageId, resolvedTraceId, eventName, service, now);
        EventEnvelope<T> envelope = buildEnvelope(eventName, payload, service, messageId, resolvedTraceId, now);

        template.convertAndSend(service.exchange(), routingKey, envelope, headers, new CorrelationData(messageId));
    }

    private MessagePostProcessor buildHeaders(
            String messageId,
            String traceId,
            String eventName,
            MessagingProps.Service service,
            Instant timestamp
    ) {
        return message -> {
            MessageProperties mp = message.getMessageProperties();
            mp.setMessageId(messageId);
            mp.setCorrelationId(traceId);
            mp.setType(eventName);
            mp.setAppId(service.sourceService());
            mp.setTimestamp(Date.from(timestamp));
            mp.setContentType(MessageProperties.CONTENT_TYPE_JSON);
            mp.setContentEncoding(CONTENT_ENCODING);
            mp.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            mp.setHeader("traceId", traceId);
            mp.setHeader("schema-version", service.schemaVersion());
            return message;
        };
    }

    private <T> EventEnvelope<T> buildEnvelope(
            String eventName,
            T payload,
            MessagingProps.Service service,
            String messageId,
            String traceId,
            Instant timestamp
    ) {
        EventEnvelope<T> envelope = new EventEnvelope<>();
        envelope.setEventType(eventName);
        envelope.setOccurredAt(timestamp);
        envelope.setPublishedAt(timestamp);
        envelope.setPayload(payload);

        EventMetadata metadata = new EventMetadata();
        metadata.setMessageId(messageId);
        metadata.setTraceId(traceId);
        metadata.setSourceService(service.sourceService());
        metadata.setSchemaVersion(service.schemaVersion());
        metadata.setPriority(EventPriority.NORMAL);
        metadata.setSensitivity(EventSensitivity.INTERNAL);

        envelope.setMetadata(metadata);
        return envelope;
    }

}
