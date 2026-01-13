package com.example.lxp.qna.adapter.in.messaging.rabbit.dto;

import java.util.List;

public record ChapterDeletedEvent(
        Long chapterId,
        List<Long> lessonIds
) {
}
