package com.example.lxp.messaging.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    String type();      // 라우팅키 생성에도 사용

    String version();   // 스키마 버전

    Map<String, String> metadata(); // traceId 등

}
