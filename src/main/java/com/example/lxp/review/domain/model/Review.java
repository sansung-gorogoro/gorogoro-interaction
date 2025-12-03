package com.example.lxp.review.domain.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
public class Review {

    // Relation Fields ----------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(nullable = false)
    private Rating rating;

    @Embedded
    @Column(nullable = false)
    private Progress progressAtReview;

    // Metadata Fields ----------

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus reviewStatus;

    // Constructors ----------

    protected Review() {}

    private Review(Long authorId, Long courseId, String title, String comment, Rating rating, Progress progressAtReview) {
        this.authorId = authorId;
        this.courseId = courseId;
        this.title = title;
        this.comment = comment;
        this.rating = rating;
        this.progressAtReview = progressAtReview;
        this.reviewStatus = ReviewStatus.PUBLISHED;
    }

    // Factory Methods ----------

    public static Review create(Long authorId, Long courseId, String title, String comment, Rating rating, Progress progressAtReview) {
        return new Review(authorId, courseId, title, comment, rating, progressAtReview);
    }

    // Helper Methods ----------

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }




}
