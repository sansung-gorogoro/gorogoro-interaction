package com.example.lxp.common.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "rabbit.events")
public record MessagingProps(
        String exchange,
        String routingPrefix,
        Queues queues,
        DeadLetters dlq
) {
    /**
     * 라우팅키 생성 헬퍼. 접두사가 정해져 있으면 붙이고, 없으면 이벤트 타입을 그대로 사용.
     */
    public String toRoutingKey(String eventType) {
        if (routingPrefix == null || routingPrefix.isBlank()) {
            return eventType;
        }
        return routingPrefix.endsWith(".") ? routingPrefix + eventType : routingPrefix;
    }

    public record Queues(
            QueueProps review,
            QueueProps qna
    ) {
    }

    public record QueueProps(
            String name,
            List<String> bindings
    ) {
    }

    public record DeadLetters(
            String dlx,
            String name,
            String routing
    ) {
    }

}
