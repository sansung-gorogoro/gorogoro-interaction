package com.example.lxp.qna.domain.model;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.exception.ErrorCode;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        indexes = {
                @Index(name = "idx_question_thread_id", columnList = "thread_id"),
                @Index(name = "idx_question_parent_id", columnList = "parent_id")
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

    private Question(Long rootId, String threadId, Long courseId, Long lessonId, Long authorId, String title, String content) {
        validate(title, content, rootId, threadId);
        this.rootId = rootId;
        this.threadId = threadId;
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.authorId = authorId;
        this.title = title;
        this.content = content;
        this.status = QuestionStatus.OPENED;
    }

    // Factory Methods ----------

    public static Question createRoot(Long courseId, Long lessonId, Long authorId, String title, String content) {
        String threadId = UUID.randomUUID().toString();
        return new Question(null, threadId, courseId, lessonId, authorId, title, content);
    }

    public static Question createReply(Long rootId, String threadId, Long courseId, Long lessonId, Long authorId, String content) {
        return new Question(rootId, threadId, courseId, lessonId, authorId, null, content);
    }

    // Business Logics ----------

    public void update(Long userId, String title, String content) {
        if (!isAuthor(userId)) {
            throw BusinessException.builder(ErrorCode.FORBIDDEN_QUESTION_MODIFICATION).build();
        }
        if (title != null && !title.isBlank()) {
            this.title = title;
        }
        if (content != null && !content.isBlank()) {
            this.content = content;
        }
    }

    public void delete(Long userId) {
        if (!isAuthor(userId)) {
            throw BusinessException.builder(ErrorCode.FORBIDDEN_QUESTION_MODIFICATION).build();
        }
        this.status = QuestionStatus.DELETED;
    }

    // MVP-002: 추후 업데이트로 질문의 상태 변화에 대한 코드 적용 예정
    public void resolve(Long userId) {
        if (!isAuthor(userId)) {
            throw BusinessException.builder(ErrorCode.FORBIDDEN_QUESTION_MODIFICATION).build();
        }
        if (this.rootId != null) { // This question is a reply
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
        }
        this.status = QuestionStatus.RESOLVED;
    }

    // Helper Methods ----------

    private boolean isAuthor(Long userId) {
        return Objects.equals(this.authorId, userId);
    }

    private void validate(String title, String content, Long rootId, String threadId) { // Renamed param to parentId
        if (content == null || content.isBlank()) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_CONTENT).build();
        }
        if (rootId == null && (title == null || title.isBlank())) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_TITLE).build();
        }
        if (threadId == null || threadId.isBlank()) {
            throw BusinessException.builder(ErrorCode.INVALID_QUESTION_OPERATION).build();
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

}
