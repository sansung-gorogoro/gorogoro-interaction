package com.example.lxp.qna.application.port.out;

import java.util.Map;
import java.util.Set;

public interface QnaUserClientPort {

    Map<Long, String> getNicknames(Set<Long> userIds);

}
