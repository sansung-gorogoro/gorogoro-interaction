package com.example.lxp.review.application.port.out;

public interface ReviewEnrollmentClientPort {

    boolean isEnrolled(Long courseId, Long userId);

}
