package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record Rating(
        @Column(nullable = false)
        Integer stars
) {
    private static final int RATING_MIN = 0;
    private static final int RATING_MAX = 5;

    public Rating {
        if (stars != null && (stars < RATING_MIN || stars > RATING_MAX)) {
            throw BusinessException.builder(ErrorCode.RATING_OUT_OF_RANGE).build();
        }
    }

    public static Rating of(Integer stars) {
        return new Rating(stars);
    }

}
