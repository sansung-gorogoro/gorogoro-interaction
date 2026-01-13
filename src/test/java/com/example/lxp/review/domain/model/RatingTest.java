package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.exception.ReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RatingTest {

    @Nested
    @DisplayName("of")
    class Of {

        @Test
        @DisplayName("별점이 0~5 사이면 생성된다")
        void shouldCreateWhenStarsWithinRange() {
            assertThat(Rating.of(0).getStars()).isEqualTo(0);
            assertThat(Rating.of(5).getStars()).isEqualTo(5);
        }

        @Test
        @DisplayName("별점이 null이면 REV-0005를 던진다")
        void shouldThrowWhenStarsIsNull() {
            assertThatThrownBy(() -> Rating.of(null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_RATING_IS_BLANK));
        }

        @Test
        @DisplayName("범위를 벗어나면 REV-0001을 던진다")
        void shouldThrowWhenStarsOutOfRange() {
            assertThatThrownBy(() -> Rating.of(-1))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.RATING_OUT_OF_RANGE));

            assertThatThrownBy(() -> Rating.of(6))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.RATING_OUT_OF_RANGE));
        }
    }
}
