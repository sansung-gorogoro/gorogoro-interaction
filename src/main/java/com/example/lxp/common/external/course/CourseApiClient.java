package com.example.lxp.common.external.course;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "course-api",
        url = "${course-api.url:http://localhost:8080}"
)
public interface CourseApiClient {

    // 원래 코드
//    @GetMapping("/api/internal/courses/{courseId}/instructor")
//    CourseInstructorCheckResponse getInstructorId(@PathVariable Long courseId);

    // Course 도메인쪽 구현 문제로 임시 사용
    @GetMapping("/api/internal/courses/{courseId}")
    Long getInstructorId(@PathVariable Long courseId);

}
