package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.UserNicknameResponse;
import com.example.lxp.review.application.port.out.UserClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewUserClientAdapter implements UserClientPort {

    private static final String UNKNOWN_NICKNAME = "알 수 없음";

    private final ReviewUserClient reviewUserClient;

    public ReviewUserClientAdapter(ReviewUserClient reviewUserClient) {
        this.reviewUserClient = reviewUserClient;
    }

    @Override
    public Map<Long, String> getNicknames(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return userIds.stream()
                .collect(Collectors.toMap(
                        userId -> userId,
                        this::fetchNicknameSafely
                ));
    }

    private String fetchNicknameSafely(Long userId) {
        try {
            UserNicknameResponse response = reviewUserClient.getNickname(userId);
            if (response == null || response.nickname() == null) {
                return UNKNOWN_NICKNAME;
            }
            return response.nickname();
        } catch (Exception ex) {
            log.warn("Failed to fetch nickname for userId={}, falling back to unknown", userId, ex);
            return UNKNOWN_NICKNAME;
        }
    }
}
