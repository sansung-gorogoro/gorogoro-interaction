package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.common.external.enrollment.EnrollmentApiClient;
import com.example.lxp.common.external.enrollment.dto.EnrollmentCheckResponse;
import com.example.lxp.qna.application.port.out.QnaEnrollmentClientPort;
import org.springframework.stereotype.Component;

@Component("qnaEnrollmentClientAdapter")
public class QnaEnrollmentClientAdapter implements QnaEnrollmentClientPort {

    private final EnrollmentApiClient enrollmentApiClient;

    public QnaEnrollmentClientAdapter(EnrollmentApiClient enrollmentApiClient) {
        this.enrollmentApiClient = enrollmentApiClient;
    }

    @Override
    public boolean isEnrolled(Long courseId, Long userId) {
        EnrollmentCheckResponse response = enrollmentApiClient.isEnrolled(courseId, userId);
        return response != null && Boolean.TRUE.equals(response.isEnrolled());
    }

}
