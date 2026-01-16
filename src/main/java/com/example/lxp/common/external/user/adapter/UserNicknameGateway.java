package com.example.lxp.common.external.user.adapter;

import com.example.lxp.common.external.user.UserApiClient;
import com.example.lxp.common.external.user.dto.UserNicknameResponse;
import com.example.lxp.common.external.user.dto.UserNicknamesResponse;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class UserNicknameGateway {

    private static final String UNKNOWN_NICKNAME = "알 수 없음";

    private static final String METHOD_SINGLE = "single";
    private static final String METHOD_BATCH = "batch";

    private static final String CB_SINGLE = "user-api-nickname";
    private static final String CB_BATCH = "user-api-nicknames";

    private static final String METRIC_PREFIX = "interaction_user_nickname_";
    private static final String INTERACTION_USER_NICKNAME_REQUESTED_IDS_TOTAL = METRIC_PREFIX + "requested_ids_total";
    private static final String INTERACTION_USER_NICKNAME_UNKNOWN_RETURNED_IDS_TOTAL = METRIC_PREFIX + "unknown_returned_ids_total";
    private static final String INTERACTION_USER_NICKNAME_NOT_FOUND_TOTAL = METRIC_PREFIX + "not_found_total";
    private static final String INTERACTION_USER_NICKNAME_BATCH_MISSING_IDS_TOTAL = METRIC_PREFIX + "batch_missing_ids_total";
    private static final String INTERACTION_USER_NICKNAME_FALLBACK_TOTAL = METRIC_PREFIX + "fallback_total";

    private static final String TAG_METHOD = "method";
    private static final String TAG_REASON = "reason";

    private static final String REASON_CB_OPEN = "cb_open";
    private static final String REASON_TIMEOUT = "timeout";
    private static final String REASON_IO = "io";
    private static final String REASON_4XX = "4xx";
    private static final String REASON_5XX = "5xx";
    private static final String REASON_FEIGN = "feign";
    private static final String REASON_OTHER = "other";

    private final UserApiClient userApiClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final MeterRegistry meterRegistry;

    public UserNicknameGateway(
            UserApiClient userApiClient,
            CircuitBreakerFactory<?, ?> circuitBreakerFactory,
            MeterRegistry meterRegistry
    ) {
        this.userApiClient = userApiClient;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.meterRegistry = meterRegistry;
    }

    public String getNickname(Long userId) {
        if (userId == null) {
            return UNKNOWN_NICKNAME;
        }

        recordRequestedIds(1, METHOD_SINGLE);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CB_SINGLE);
        return circuitBreaker.run(
                () -> {
                    try {
                        UserNicknameResponse response = userApiClient.getNickname(userId);
                        String nickname = response != null ? response.nickname() : null;
                        if (nickname == null) {
                            recordUnknownReturned(1, METHOD_SINGLE);
                            return UNKNOWN_NICKNAME;
                        }
                        return nickname;
                    } catch (FeignException.NotFound notFound) {
                        recordNotFound();
                        recordUnknownReturned(1, METHOD_SINGLE);
                        return UNKNOWN_NICKNAME;
                    }
                },
                throwable -> {
                    recordFallback(METHOD_SINGLE, classifyReason(throwable));
                    recordUnknownReturned(1, METHOD_SINGLE);
                    return UNKNOWN_NICKNAME;
                }
        );
    }

    public Map<Long, String> getNicknames(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        int requestedCount = userIds.size();
        recordRequestedIds(requestedCount, METHOD_BATCH);

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create(CB_BATCH);
        return circuitBreaker.run(
                () -> {
                    UserNicknamesResponse response = userApiClient.getNicknames(List.copyOf(userIds));

                    Map<Long, String> result = new HashMap<>();
                    if (response != null && response.nicknames() != null) {
                        result.putAll(response.nicknames());
                    }

                    Set<Long> missing = new HashSet<>(userIds);
                    missing.removeAll(result.keySet());
                    if (response != null && response.missingUserIds() != null) {
                        missing.addAll(response.missingUserIds());
                    }

                    if (!missing.isEmpty()) {
                        int missingCount = missing.size();
                        recordBatchMissingIds(missingCount);
                        recordUnknownReturned(missingCount, METHOD_BATCH);
                        for (Long userId : missing) {
                            result.put(userId, UNKNOWN_NICKNAME);
                        }
                    }

                    return result;
                },
                throwable -> {
                    recordFallback(METHOD_BATCH, classifyReason(throwable));
                    recordUnknownReturned(requestedCount, METHOD_BATCH);
                    return createUnknownMap(userIds);
                }
        );
    }

    private Map<Long, String> createUnknownMap(Set<Long> userIds) {
        Map<Long, String> result = new HashMap<>();
        for (Long userId : userIds) {
            result.put(userId, UNKNOWN_NICKNAME);
        }
        return result;
    }

    private void recordRequestedIds(int count, String method) {
        meterRegistry.counter(INTERACTION_USER_NICKNAME_REQUESTED_IDS_TOTAL, TAG_METHOD, method).increment(count);
    }

    private void recordUnknownReturned(int count, String method) {
        meterRegistry.counter(INTERACTION_USER_NICKNAME_UNKNOWN_RETURNED_IDS_TOTAL, TAG_METHOD, method).increment(count);
    }

    private void recordNotFound() {
        meterRegistry.counter(INTERACTION_USER_NICKNAME_NOT_FOUND_TOTAL).increment();
    }

    private void recordBatchMissingIds(int missingCount) {
        meterRegistry.counter(INTERACTION_USER_NICKNAME_BATCH_MISSING_IDS_TOTAL).increment(missingCount);
    }

    private void recordFallback(String method, String reason) {
        meterRegistry.counter(INTERACTION_USER_NICKNAME_FALLBACK_TOTAL, TAG_METHOD, method, TAG_REASON, reason).increment();
    }

    private String classifyReason(Throwable throwable) {
        if (throwable instanceof CallNotPermittedException) {
            return REASON_CB_OPEN;
        }
        if (throwable instanceof TimeoutException) {
            return REASON_TIMEOUT;
        }
        if (throwable instanceof IOException) {
            return REASON_IO;
        }
        if (throwable instanceof FeignException.FeignClientException) {
            return REASON_4XX;
        }
        if (throwable instanceof FeignException.FeignServerException) {
            return REASON_5XX;
        }
        if (throwable instanceof FeignException) {
            return REASON_FEIGN;
        }
        return REASON_OTHER;
    }

}
