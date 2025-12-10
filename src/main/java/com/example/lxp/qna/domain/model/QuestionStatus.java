package com.example.lxp.qna.domain.model;

public enum QuestionStatus {
    OPENED,       // Question is active and awaiting answers
    RESOLVED,   // Question has been satisfactorily answered
    CLOSED,     // Question thread is closed (e.g., by instructor or admin)
    DELETED     // Question has been deleted
}