package com.example.lxp.review.adapter.out.external;

import com.example.lxp.common.external.user.adapter.UserNicknameGateway;
import com.example.lxp.review.application.port.out.ReviewUserClientPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class ReviewUserClientAdapter implements ReviewUserClientPort {

    private final UserNicknameGateway userNicknameGateway;

    public ReviewUserClientAdapter(UserNicknameGateway userNicknameGateway) {
        this.userNicknameGateway = userNicknameGateway;
    }

    @Override
    public Map<Long, String> getNicknames(Set<Long> userIds) {
        return userNicknameGateway.getNicknames(userIds);
    }
}
