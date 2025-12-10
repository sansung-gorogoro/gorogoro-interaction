package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.application.port.out.VerifyInstructorPort;
import org.springframework.stereotype.Component;

@Component
public class CourseClientAdapter implements VerifyInstructorPort {

    @Override
    public boolean isInstructor(Long instructorId) {
        // TODO
        return true;
    }
}
