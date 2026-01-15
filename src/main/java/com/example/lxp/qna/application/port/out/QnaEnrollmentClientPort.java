package com.example.lxp.qna.application.port.out;

public interface QnaEnrollmentClientPort {

    boolean isEnrolled(Long courseId, Long userId);

}
