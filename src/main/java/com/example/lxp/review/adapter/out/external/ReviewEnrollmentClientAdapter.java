package com.example.lxp.review.adapter.out.external;

import com.example.lxp.common.external.enrollment.EnrollmentApiClient;
import com.example.lxp.review.application.port.out.ReviewEnrollmentClientPort;
import org.springframework.stereotype.Component;

@Component
public class ReviewEnrollmentClientAdapter implements ReviewEnrollmentClientPort {

    private final EnrollmentApiClient enrollmentApiClient;

    public ReviewEnrollmentClientAdapter(EnrollmentApiClient enrollmentApiClient) {
        this.enrollmentApiClient = enrollmentApiClient;
    }

    @Override
    public boolean isEnrolled(Long courseId, Long userId) {
//        EnrollmentCheckResponse response = enrollmentApiClient.isEnrolled(courseId, userId);
//        return response != null && Boolean.TRUE.equals(response.isEnrolled());
        return enrollmentApiClient.isEnrolled(courseId, userId);
    }

}
