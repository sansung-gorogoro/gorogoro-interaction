package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

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

    public Integer getStars() {
        return stars;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rating r)) return false;
        return Objects.equals(this.stars, r.stars);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stars);
    }

}
