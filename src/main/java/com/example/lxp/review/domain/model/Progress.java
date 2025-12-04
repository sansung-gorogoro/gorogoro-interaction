package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Progress {

    @Column(nullable = true)
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Progress p)) return false;
        return Objects.equals(this.progress, p.progress);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(progress);
    }

}
