package com.example.lxp.review.adapter.in.messaging.rabbit;

import com.example.lxp.exception.BusinessException;
import com.example.lxp.review.adapter.in.messaging.rabbit.dto.CourseDeletedEvent;
import com.example.lxp.review.application.port.in.ReviewIntegrationUseCase;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByCourseCommand;
import com.example.lxp.review.exception.ReviewErrorCode;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReviewRabbitEventSubscriber {

    private final ReviewIntegrationUseCase reviewIntegrationUseCase;

    public ReviewRabbitEventSubscriber(ReviewIntegrationUseCase reviewIntegrationUseCase) {
        this.reviewIntegrationUseCase = reviewIntegrationUseCase;
    }

    @RabbitListener(queues = "${rabbit.events.queues.review.name}", ackMode = "MANUAL")
    public void handle(CourseDeletedEvent payload, Message message, Channel channel) throws IOException {
        long tag = message.getMessageProperties().getDeliveryTag();

        validate(payload);
        DeleteReviewsByCourseCommand command = new DeleteReviewsByCourseCommand(payload.courseId());

        try {
            reviewIntegrationUseCase.deleteReviewsByCourseId(command);
            channel.basicAck(tag, false);
        } catch (Exception e) {
            // TODO: Logging, requeue, etc.
            // 재큐(false)로 NACK → DLX로 이동 → DLQ에 도착
            channel.basicNack(tag, false, true);
        }
    }

    private void validate(CourseDeletedEvent payload) {
        if (payload.courseId() == null) {
            throw BusinessException.builder(ReviewErrorCode.EVENT_COURSE_ID_IS_BLANK).build();
        }
    }

}
