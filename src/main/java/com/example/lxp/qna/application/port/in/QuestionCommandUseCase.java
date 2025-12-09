package com.example.lxp.qna.application.port.in;

import com.example.lxp.qna.application.port.in.dto.CreateQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.DeleteQuestionCommand;
import com.example.lxp.qna.application.port.in.dto.UpdateQuestionCommand;
import com.example.lxp.qna.domain.model.Question;

public interface QuestionCommandUseCase {

    Question createQuestion(CreateQuestionCommand command);

    Question updateQuestion(UpdateQuestionCommand command);

    void deleteQuestion(DeleteQuestionCommand command);

}

