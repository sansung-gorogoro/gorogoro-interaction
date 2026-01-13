package com.example.lxp.qna.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.qna.exception.QnaErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_question_thread_id", columnList = "thread_id"),
                @Index(name = "idx_question_root_id", columnList = "root_id")
        }
)
public class Question {

    // Relation Fields ----------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "root_id", nullable = true)
    private Long rootId;

    @Column(name = "thread_id", nullable = false, length = 36)
    private String threadId;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long lessonId;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = true)
    private Long instructorId;

    @Column(nullable = true)
    private Instant lastActivityAt;

    // Content Fields ----------

    @Column(nullable = true)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // Metadata Fields ----------

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private QuestionStatus status;

    // Constructors ----------

    protected Question() {}

    private Question(Long rootId, String threadId, Long courseId, Long lessonId, Long authorId,
                     Long instructorId, String title, String content) {
        validate(title, content, rootId, threadId);
        this.rootId = rootId;
        this.threadId = threadId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.authorId = authorId;
        this.instructorId = instructorId;
        this.title = title;
        this.content = content;
        if (rootId == null) {
            this.status = QuestionStatus.OPENED;
            this.lastActivityAt = Instant.now();
        }
    }

    // Factory Methods ----------

    public static Question createRoot(Long courseId, Long lessonId, Long authorId, Long instructorId,
                                       String title, String content) {
        String threadId = UUID.randomUUID().toString();
        return new Question(null, threadId, courseId, lessonId, authorId, instructorId, title, content);
    }

    public static Question createReply(Long rootId, String threadId, Long courseId, Long lessonId,
                                        Long authorId, String content) {
        return new Question(rootId, threadId, courseId, lessonId, authorId, null, null, content);
    }

    // Business Logics ----------

    public void update(String title, String content) {
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public void delete() {
        this.status = QuestionStatus.DELETED;
    }

    public void markAsAnswered() {
        this.status = QuestionStatus.ANSWERED;
        this.lastActivityAt = Instant.now();
    }

    public void reopen() {
        this.status = QuestionStatus.OPENED;
        this.lastActivityAt = Instant.now();
    }

    public void updateLastActivity() {
        this.lastActivityAt = Instant.now();
    }

    // Helper Methods ----------

    public boolean isRoot() {
        return this.rootId == null;
    }

    public boolean isInstructor(Long userId) {
        return Objects.equals(this.instructorId, userId);
    }

    public boolean isAuthor(Long userId) {
        return Objects.equals(this.authorId, userId);
    }

    private void validate(String title, String content, Long rootId, String threadId) {
        if (content == null || content.isBlank()) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_CONTENT_IS_BLANK).build();
        }
        if (rootId == null && (title == null || title.isBlank())) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_TITLE_IS_BLANK).build();
        }
        if (threadId == null || threadId.isBlank()) {
            throw BusinessException.builder(QnaErrorCode.QUESTION_THREAD_ID_IS_BLANK).build();
        }
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Getters ----------

    public Long getId() {
        return id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public Long getLessonId() {
        return lessonId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public Long getRootId() {
        return rootId;
    }

    public String getThreadId() {
        return threadId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public Long getInstructorId() {
        return instructorId;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

}
