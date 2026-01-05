package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.qna.adapter.out.external.dto.QnaEnrollmentCheckResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "qna-enrollment-api",
        url = "${enrollment-api.url:http://localhost:8080}"
)
public interface QnaEnrollmentClient {

    @GetMapping("/api/enrollments")
    QnaEnrollmentCheckResponse isEnrolled(
            @RequestParam Long courseId,
            @RequestParam Long userId
    );

}
