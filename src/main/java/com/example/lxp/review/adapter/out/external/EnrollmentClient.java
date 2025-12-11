package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.EnrollmentCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "enrollment-api",
        url = "${enrollment-api.url:http://localhost:8080}"
)
public interface EnrollmentClient {

    @GetMapping("/api/enrollments")
    EnrollmentCheckResponse isEnrolled(
            @RequestParam Long courseId,
            @RequestParam Long instructorId
    );

}
