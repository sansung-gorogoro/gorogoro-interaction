package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Rating {

    @Column(nullable = false)
    private Integer stars;

    // JPA REQUIRED
    protected Rating() {}

    private Rating(Integer stars) {
        this.stars = stars;
    }

    public static Rating of(Integer stars) {
        if (stars < 0 || stars > 5) {
            throw BusinessException.builder(ErrorCode.RATING_OUT_OF_RANGE).build();
        }
        return new Rating(stars);
    }

}
