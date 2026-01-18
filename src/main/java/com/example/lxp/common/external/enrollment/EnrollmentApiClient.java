package com.example.lxp.common.external.enrollment;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "enrollment-api",
        url = "${enrollment-api.url:http://localhost:8080}"
)
public interface EnrollmentApiClient {

//    @GetMapping("/api/enrollments")
//    EnrollmentCheckResponse isEnrolled(
//            @RequestParam Long courseId,
//            @RequestParam Long userId
//    );

    @GetMapping("/api/internal/enrollments/existence")
    boolean isEnrolled(
            @RequestParam Long courseId,
            @RequestParam Long userId
    );

}
