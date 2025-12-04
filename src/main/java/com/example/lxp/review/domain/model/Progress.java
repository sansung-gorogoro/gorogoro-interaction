package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Progress(
        @Column(nullable = false)
        Integer progress
) {
    private static final int PROGRESS_MIN = 0;
    private static final int PROGRESS_MAX = 100;

    public Progress {
        if (progress != null && (progress < PROGRESS_MIN || progress > PROGRESS_MAX)) {
            throw BusinessException.builder(ErrorCode.PROGRESS_OUT_OF_RANGE).build();
        }
    }

    public static Progress of(Integer progress) {
        return new Progress(progress);
    }

    public Integer getProgress() {
        return progress;
    }
    
}
