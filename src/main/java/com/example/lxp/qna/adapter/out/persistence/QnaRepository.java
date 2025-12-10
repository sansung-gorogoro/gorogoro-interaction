package com.example.lxp.qna.adapter.out.persistence;

import com.example.lxp.qna.domain.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Question, Long> {

    void deleteByThreadId(String threadId);
}
