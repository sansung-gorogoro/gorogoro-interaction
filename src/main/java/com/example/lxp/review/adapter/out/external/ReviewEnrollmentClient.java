package com.example.lxp.review.adapter.out.external;

import com.example.lxp.review.adapter.out.external.dto.ReviewEnrollmentCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "review-enrollment-api",
        url = "${enrollment-api.url:http://localhost:8080}"
)
public interface ReviewEnrollmentClient {

    @GetMapping("/api/enrollments")
    ReviewEnrollmentCheckResponse isEnrolled(
            @RequestParam Long courseId,
            @RequestParam Long userId
    );

}
