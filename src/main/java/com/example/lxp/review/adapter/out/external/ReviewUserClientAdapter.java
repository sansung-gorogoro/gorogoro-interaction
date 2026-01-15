package com.example.lxp.review.adapter.out.external;

import com.example.lxp.common.external.user.UserApiClient;
import com.example.lxp.common.external.user.dto.UserNicknamesResponse;
import com.example.lxp.review.application.port.out.ReviewUserClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ReviewUserClientAdapter implements ReviewUserClientPort {

    private static final String UNKNOWN_NICKNAME = "알 수 없음";

    private final UserApiClient userApiClient;

    public ReviewUserClientAdapter(UserApiClient userApiClient) {
        this.userApiClient = userApiClient;
    }

    @Override
    public Map<Long, String> getNicknames(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            UserNicknamesResponse response = userApiClient.getNicknames(List.copyOf(userIds));
            return mergeWithUnknowns(userIds, response);
        } catch (Exception ex) {
            log.warn("Failed to fetch nicknames for userIds={}, falling back to unknown", userIds, ex);
            return createUnknownMap(userIds);
        }
    }

    private Map<Long, String> mergeWithUnknowns(Set<Long> userIds, UserNicknamesResponse response) {
        Map<Long, String> result = new HashMap<>();
        if (response != null && response.nicknames() != null) {
            result.putAll(response.nicknames());
        }

        Set<Long> missing = new HashSet<>(userIds);
        missing.removeAll(result.keySet());
        if (response != null && response.missingUserIds() != null) {
            missing.addAll(response.missingUserIds());
        }

        for (Long userId : missing) {
            result.put(userId, UNKNOWN_NICKNAME);
        }

        return result;
    }

    private Map<Long, String> createUnknownMap(Set<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        for (Long userId : userIds) {
            result.put(userId, UNKNOWN_NICKNAME);
        }
        return result;
    }
}
