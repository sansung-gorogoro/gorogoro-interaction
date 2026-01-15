package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.exception.ReviewErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Rating {

    private static final int RATING_MIN = 0;
    private static final int RATING_MAX = 5;

    @Column(nullable = false)
    private Integer stars;

    protected Rating() {}

    private Rating(Integer stars) {
        this.stars = stars;
    }

    public static Rating of(Integer stars) {
        if (stars == null) {
            throw BusinessException.builder(ReviewErrorCode.REVIEW_RATING_IS_BLANK).build();
        }
        if (stars < RATING_MIN || stars > RATING_MAX) {
            throw BusinessException.builder(ReviewErrorCode.RATING_OUT_OF_RANGE).build();
        }
        return new Rating(stars);
    }

    public Integer getStars() {
        return stars;
    }

}
