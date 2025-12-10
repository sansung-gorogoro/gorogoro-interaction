package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.application.port.out.CheckEnrollmentPort;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentClientAdapter implements CheckEnrollmentPort {

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        return true;
    }

}
