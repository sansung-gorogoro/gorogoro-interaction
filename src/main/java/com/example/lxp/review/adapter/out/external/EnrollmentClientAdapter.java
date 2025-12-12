package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.EnrollmentCheckResponse;
import com.example.lxp.review.application.port.out.EnrollmentClientPort;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentClientAdapter implements EnrollmentClientPort {

    private final EnrollmentClient enrollmentClient;

    public EnrollmentClientAdapter(EnrollmentClient enrollmentClient) {
        this.enrollmentClient = enrollmentClient;
    }

    @Override
    public boolean isEnrolled(Long courseId, Long userId) {
        EnrollmentCheckResponse response = enrollmentClient.isEnrolled(courseId, userId);
        return response != null && Boolean.TRUE.equals(response.isEnrolled());
    }

}
