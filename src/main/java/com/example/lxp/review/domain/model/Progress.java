package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.exception.ReviewErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Progress {

    private static final int PROGRESS_MIN = 0;
    private static final int PROGRESS_MAX = 100;

    @Column(nullable = false)
    private Integer progress;

    protected Progress() {}

    private Progress(Integer progress) {
        this.progress = progress;
    }

    public static Progress of(Integer progress) {
        if (progress != null && (progress < PROGRESS_MIN || progress > PROGRESS_MAX)) {
            throw BusinessException.builder(ReviewErrorCode.PROGRESS_OUT_OF_RANGE).build();
        }
        return new Progress(progress);
    }

    public Integer getProgress() {
        return progress;
    }

}
