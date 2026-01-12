package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.ReviewEnrollmentCheckResponse;
import com.example.lxp.review.application.port.out.EnrollmentClientPort;
import org.springframework.stereotype.Component;

@Component
public class ReviewEnrollmentClientAdapter implements EnrollmentClientPort {

    private final ReviewEnrollmentClient enrollmentClient;

    public ReviewEnrollmentClientAdapter(ReviewEnrollmentClient enrollmentClient) {
        this.enrollmentClient = enrollmentClient;
    }

    @Override
    public boolean isEnrolled(Long courseId, Long userId) {
        ReviewEnrollmentCheckResponse response = enrollmentClient.isEnrolled(courseId, userId);
        return response != null && Boolean.TRUE.equals(response.isEnrolled());
    }

}
