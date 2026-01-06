package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.adapter.out.external.dto.CourseInstructorCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-api",
        url = "${course-api.url:http://localhost:8080}"
)
public interface CourseClient {

    @GetMapping("/api/courses/{courseId}/instructor")
    CourseInstructorCheckResponse getInstructorId(@PathVariable Long courseId);

}
