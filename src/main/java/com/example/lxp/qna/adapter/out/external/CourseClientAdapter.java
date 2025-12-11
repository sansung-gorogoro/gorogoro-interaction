package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.adapter.out.external.dto.CourseInstructorCheckResponse;
import com.example.lxp.qna.application.port.out.CourseClientPort;
import org.springframework.stereotype.Component;

@Component
public class CourseClientAdapter implements CourseClientPort {

    private final CourseClient courseClient;

    public CourseClientAdapter(CourseClient courseClient) {
        this.courseClient = courseClient;
    }

    @Override
    public boolean isInstructor(Long courseId, Long userId) {
        CourseInstructorCheckResponse response = courseClient.isInstructor(courseId, userId);
        return response != null && Boolean.TRUE.equals(response.isInstructor());
    }

}
