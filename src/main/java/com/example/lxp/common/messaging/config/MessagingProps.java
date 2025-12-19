package com.example.lxp.common.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

@ConfigurationProperties(prefix = "rabbit")
public record MessagingProps(
        Shared shared,
        Map<String, Service> services
) {

    public record Shared(
            String dlx,
            String dlq
    ) {
    }

    public record Service(
            String prefix,
            String exchange,
            Map<String, QueueConfig> queues,
            String schemaVersion,
            String sourceService
    ) {
    }

    public record QueueConfig(
            String name,
            List<String> bindings
    ) {
    }
}
