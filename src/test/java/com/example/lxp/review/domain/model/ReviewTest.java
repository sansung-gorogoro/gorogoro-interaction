package com.example.lxp.review.domain.model;

import com.example.lxp.common.auth.model.User;
import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.exception.ReviewErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReviewTest {

    private static final long AUTHOR_ID = 10L;
    private static final long OTHER_ID = 99L;
    private static final long COURSE_ID = 1L;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("필수 값이 모두 있으면 생성된다")
        void shouldCreateWhenFieldsAreValid() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "title", "comment", Rating.of(5));
            assertThat(review.getAuthorId()).isEqualTo(AUTHOR_ID);
            assertThat(review.getCourseId()).isEqualTo(COURSE_ID);
            assertThat(review.getTitle()).isEqualTo("title");
            assertThat(review.getComment()).isEqualTo("comment");
            assertThat(review.getRating().getStars()).isEqualTo(5);
        }

        @Test
        @DisplayName("제목이 null/blank이면 REV-0003")
        void shouldThrowWhenTitleInvalid() {
            assertThatThrownBy(() -> Review.create(AUTHOR_ID, COURSE_ID, null, "c", Rating.of(5)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_TITLE_IS_BLANK));

            assertThatThrownBy(() -> Review.create(AUTHOR_ID, COURSE_ID, "  ", "c", Rating.of(5)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_TITLE_IS_BLANK));
        }

        @Test
        @DisplayName("내용이 null/blank이면 REV-0004")
        void shouldThrowWhenCommentInvalid() {
            assertThatThrownBy(() -> Review.create(AUTHOR_ID, COURSE_ID, "t", null, Rating.of(5)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_COMMENT_IS_BLANK));

            assertThatThrownBy(() -> Review.create(AUTHOR_ID, COURSE_ID, "t", "   ", Rating.of(5)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_COMMENT_IS_BLANK));
        }

        @Test
        @DisplayName("평점이 null이면 REV-0005")
        void shouldThrowWhenRatingIsNull() {
            assertThatThrownBy(() -> Review.create(AUTHOR_ID, COURSE_ID, "t", "c", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_RATING_IS_BLANK));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("작성자가 아니면 REV-0007")
        void shouldThrowWhenNotAuthor() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "t", "c", Rating.of(5));
            assertThatThrownBy(() -> review.update(OTHER_ID, "nt", "nc", Rating.of(4)))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.FORBIDDEN_REVIEW_MODIFICATION));
        }

        @Test
        @DisplayName("제목을 공백으로 수정하면 REV-0003")
        void shouldThrowWhenTitleBlankOnUpdate() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "t", "c", Rating.of(5));
            assertThatThrownBy(() -> review.update(AUTHOR_ID, "  ", null, null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_TITLE_IS_BLANK));
        }

        @Test
        @DisplayName("내용을 공백으로 수정하면 REV-0004")
        void shouldThrowWhenCommentBlankOnUpdate() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "t", "c", Rating.of(5));
            assertThatThrownBy(() -> review.update(AUTHOR_ID, null, "  ", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getCode())
                            .isEqualTo(ReviewErrorCode.REVIEW_COMMENT_IS_BLANK));
        }

        @Test
        @DisplayName("부분 업데이트: null은 기존 값을 유지한다")
        void shouldKeepOriginalWhenFieldIsNull() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "t", "c", Rating.of(5));
            review.update(AUTHOR_ID, null, null, null);
            assertThat(review.getTitle()).isEqualTo("t");
            assertThat(review.getComment()).isEqualTo("c");
            assertThat(review.getRating().getStars()).isEqualTo(5);
        }

        @Test
        @DisplayName("제목/내용/평점이 올바르면 수정된다")
        void shouldUpdateWhenFieldsValid() {
            Review review = Review.create(AUTHOR_ID, COURSE_ID, "t", "c", Rating.of(5));
            review.update(AUTHOR_ID, "nt", "nc", Rating.of(3));
            assertThat(review.getTitle()).isEqualTo("nt");
            assertThat(review.getComment()).isEqualTo("nc");
            assertThat(review.getRating().getStars()).isEqualTo(3);
        }
    }
}
