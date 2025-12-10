package com.example.lxp.review.adapter.out.persistence;

import com.example.lxp.common.port.out.external.PublishEventPort;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherAdapter implements PublishEventPort {

    @Override
    public void publish(Object event) {
        // TODO
    }

}
