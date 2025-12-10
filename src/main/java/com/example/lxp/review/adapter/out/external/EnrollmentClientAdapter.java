package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.application.port.out.CheckEnrollmentPort;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentClientAdapter implements CheckEnrollmentPort {

    @Override
    public boolean isEnrolled(Long userId, Long courseId) {
        // TODO: This is a placeholder implementation.
        // This method would make an API call to an external enrollment service to verify if the user is enrolled in the course.
        return true;
    }
    
}
