package com.example.lxp.common.external.course;

import com.example.lxp.common.external.course.dto.CourseInstructorCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-api",
        url = "${course-api.url:http://localhost:8080}"
)
public interface CourseApiClient {

    @GetMapping("/api/courses/{courseId}/instructor")
    CourseInstructorCheckResponse getInstructorId(@PathVariable Long courseId);

}
