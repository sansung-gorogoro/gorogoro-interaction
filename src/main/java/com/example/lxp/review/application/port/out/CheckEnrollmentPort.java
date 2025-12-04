package com.example.lxp.review.application.port.out;

public interface CheckEnrollmentPort {

    boolean isEnrolled(Long userId, Long courseId);

}
