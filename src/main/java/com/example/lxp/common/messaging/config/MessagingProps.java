package com.example.lxp.common.messaging.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbit.config")
public record MessagingProps(

        String exchange,
        String routing

) {
    /**
     * 라우팅키 생성 헬퍼. 접두사가 정해져 있으면 붙이고, 없으면 이벤트 타입을 그대로 사용.
     */
    public String toRoutingKey(String eventType) {
        if (routing == null || routing.isBlank()) {
            return eventType;
        }
        // routing 값이 이미 완전한 키라면 그대로 사용 (POC)
        return routing.endsWith(".") ? routing + eventType : routing;
    }

}
