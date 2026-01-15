package com.example.lxp.review.application.port.out;

import java.util.Map;
import java.util.Set;

public interface ReviewUserClientPort {

    Map<Long, String> getNicknames(Set<Long> userIds);
    
}
