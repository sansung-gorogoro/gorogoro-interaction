package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.adapter.out.external.dto.QnaEnrollmentCheckResponse;
import com.example.lxp.qna.application.port.out.EnrollmentClientPort;
import org.springframework.stereotype.Component;

@Component("qnaEnrollmentClientAdapter")
public class QnaEnrollmentClientAdapter implements EnrollmentClientPort {

    private final QnaEnrollmentClient qnaEnrollmentClient;

    public QnaEnrollmentClientAdapter(QnaEnrollmentClient qnaEnrollmentClient) {
        this.qnaEnrollmentClient = qnaEnrollmentClient;
    }

    @Override
    public boolean isEnrolled(Long courseId, Long userId) {
        QnaEnrollmentCheckResponse response = qnaEnrollmentClient.isEnrolled(courseId, userId);
        return response != null && Boolean.TRUE.equals(response.isEnrolled());
    }

}
