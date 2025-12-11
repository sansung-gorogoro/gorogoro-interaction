package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.adapter.out.external.dto.CourseInstructorCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "course-api",
        url = "${course-api.url:http://localhost:8080}"
)
public interface CourseClient {

    @GetMapping("/api/courses/{courseId}")
    CourseInstructorCheckResponse isInstructor(
            @PathVariable Long courseId,
            @RequestParam Long instructorId
    );

}
