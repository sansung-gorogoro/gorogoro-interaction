package com.example.lxp.common.messaging.domain.model;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record EventEnvelope<T extends DomainEvent>(
        UUID eventId,
        Instant occurredAt,
        T payload,
        Map<String, String> metadata
) {
    /**
     * **CAUTION**
     * <p>
     * DO NOT USE NATIVE CONSTRUCTOR OUTSIDE THE RECORD CLASS
     * <p>
     * Use {@code EventEnvelop.wrap()} factory method instead
     */
    public EventEnvelope {
        Optional.ofNullable(eventId).orElseThrow(() -> new IllegalArgumentException("eventId is required"));
        Optional.ofNullable(occurredAt).orElseThrow(() -> new IllegalArgumentException("occurredAt is required"));
        Optional.ofNullable(payload).orElseThrow(() -> new IllegalArgumentException("payload is required"));
        metadata = metadata == null ? Collections.emptyMap() : Map.copyOf(metadata);
    }

    public static <T extends DomainEvent> EventEnvelope<T> wrap(T payload) {
        return new EventEnvelope<>(UUID.randomUUID(), Instant.now(), payload, Collections.emptyMap());
    }

    public EventEnvelope<T> withMetadata(Map<String, String> additional) {
        if (additional == null || additional.isEmpty()) {
            return this;
        }
        var merged = new HashMap<>(this.metadata());
        merged.putAll(additional);
        return new EventEnvelope<>(eventId, occurredAt, payload, merged);
    }

    public String type() {
        return payload.type();
    }

    public String version() {
        return payload.version();
    }

}
