package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Progress {

    @Column(nullable = false)
    private Integer progress;

    // JPA REQUIRED
    protected Progress() {}

    private Progress(Integer progress) {
        this.progress = progress;
    }

    public static Progress of(Integer progress) {
        if (progress < 0 || progress > 100) {
            throw BusinessException.builder(ErrorCode.PROGRESS_OUT_OF_RANGE).build();
        }
        return new Progress(progress);
    }
}
