package com.example.lxp.review.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.exception.ReviewErrorCode;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "uk_author_id_and_course_id", columnNames = {"author_id", "course_id"})
})
public class Review {

    // Relation Fields ----------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false)
    private Long courseId;

    // Contents Fields ----------

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String comment;

    @Embedded
    private Rating rating;

    @Embedded
    // Not included in the first milestone, but will be implemented later on second milestone.
    private Progress progressAtReview;

    // Metadata Fields ----------

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus reviewStatus;

    // Constructors ----------

    protected Review() {}

    private Review(Long authorId, Long courseId, String title, String comment, Rating rating) {
        validate(title, comment, rating);
        this.authorId = authorId;
        this.courseId = courseId;
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.reviewStatus = ReviewStatus.PUBLISHED;
    }

    // Factory Methods ----------

    public static Review create(Long authorId, Long courseId, String title, String comment, Rating rating) {
        return new Review(authorId, courseId, title, comment, rating);
    }

    // Business Logics ----------

    public void update(Long userId, String title, String comment, Rating rating) {
        if (isAuthor(userId)) {
            if (title != null) {
                this.title = title;
            }
            if (comment != null) {
                this.comment = comment;
            }
            if (rating != null) {
                this.rating = rating;
            }
        } else {
            throw BusinessException.builder(ReviewErrorCode.FORBIDDEN_REVIEW_MODIFICATION).build();
        }
    }

    // Helper Methods ----------

    private boolean isAuthor(Long userId) {
        return Objects.equals(this.authorId, userId);
    }

    private void validate(String title, String comment, Rating rating) {
        if (title == null || title.isBlank()) {
            throw BusinessException.builder(ReviewErrorCode.INVALID_REVIEW_TITLE).build();
        }
        if (comment == null || comment.isBlank()) {
            throw BusinessException.builder(ReviewErrorCode.INVALID_REVIEW_COMMENT).build();
        }
        if (rating == null) {
            throw BusinessException.builder(ReviewErrorCode.INVALID_REVIEW_RATING).build();
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // Getters ----------

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public Rating getRating() {
        return rating;
    }

    public Progress getProgressAtReview() {
        return progressAtReview;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }

}
