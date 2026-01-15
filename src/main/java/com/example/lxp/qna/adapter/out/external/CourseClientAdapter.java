package com.example.lxp.qna.adapter.out.external;

import com.example.lxp.common.external.course.CourseApiClient;
import com.example.lxp.common.external.course.dto.CourseInstructorCheckResponse;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ExternalServiceErrorCode;
import com.example.lxp.qna.application.port.out.CourseClientPort;
import feign.FeignException;
import feign.RetryableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CourseClientAdapter implements CourseClientPort {

    private static final Logger log = LoggerFactory.getLogger(CourseClientAdapter.class);

    private final CourseApiClient courseClient;

    public CourseClientAdapter(CourseApiClient courseClient) {
        this.courseClient = courseClient;
    }

    @Override
    public Long getInstructorId(Long courseId) {
        try {
            CourseInstructorCheckResponse response = courseClient.getInstructorId(courseId);
            if (response == null || response.instructorId() == null) {
                log.error("course-api getInstructorId returned null for courseId={}", courseId);
                throw BusinessException.builder(ExternalServiceErrorCode.DEPENDENCY_CONTRACT_ERROR).build();
            }
            return response.instructorId();
        } catch (BusinessException e) {
            throw e;
        } catch (RetryableException e) {
            log.error("course-api getInstructorId timeout for courseId={}", courseId, e);
            throw BusinessException.builder(ExternalServiceErrorCode.DEPENDENCY_TIMEOUT).withCause(e).build();
        } catch (FeignException.ServiceUnavailable e) {
            log.error("course-api getInstructorId unavailable for courseId={}", courseId, e);
            throw BusinessException.builder(ExternalServiceErrorCode.DEPENDENCY_UNAVAILABLE).withCause(e).build();
        } catch (FeignException e) {
            log.error("course-api getInstructorId failed for courseId={}, status={}", courseId, e.status(), e);
            throw BusinessException.builder(ExternalServiceErrorCode.DEPENDENCY_BAD_RESPONSE).withCause(e).build();
        }
    }

}
