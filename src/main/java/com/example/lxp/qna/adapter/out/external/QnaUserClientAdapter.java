package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.common.external.user.adapter.UserNicknameGateway;
import com.example.lxp.qna.application.port.out.QnaUserClientPort;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class QnaUserClientAdapter implements QnaUserClientPort {

    private final UserNicknameGateway userNicknameGateway;

    public QnaUserClientAdapter(UserNicknameGateway userNicknameGateway) {
        this.userNicknameGateway = userNicknameGateway;
    }

    @Override
    public Map<Long, String> getNicknames(Set<Long> userIds) {
        return userNicknameGateway.getNicknames(userIds);
    }
}
