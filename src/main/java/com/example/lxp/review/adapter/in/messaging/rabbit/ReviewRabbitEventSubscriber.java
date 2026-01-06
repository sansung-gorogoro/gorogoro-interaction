package com.example.lxp.review.adapter.in.messaging.rabbit;

import com.example.lxp.common.messaging.domain.model.EventEnvelope;
import com.example.lxp.review.adapter.in.messaging.rabbit.dto.CourseDeletedEvent;
import com.example.lxp.review.adapter.in.messaging.rabbit.dto.UserDeletedEvent;
import com.example.lxp.review.application.port.in.ReviewIntegrationUseCase;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByCourseCommand;
import com.example.lxp.review.application.port.in.dto.DeleteReviewsByUserCommand;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReviewRabbitEventSubscriber {

    private final ReviewIntegrationUseCase reviewIntegrationUseCase;

    public ReviewRabbitEventSubscriber(ReviewIntegrationUseCase reviewIntegrationUseCase) {
        this.reviewIntegrationUseCase = reviewIntegrationUseCase;
    }

    @RabbitListener(queues = "${rabbit.services.review.queues.course-deleted.name}", ackMode = "MANUAL")
    public void handleCourseDeleted(
            @Payload EventEnvelope<CourseDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.printf("Review Service consume: routingKey=%s messageId=%s traceId=%s%n",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");
            DeleteReviewsByCourseCommand command = new DeleteReviewsByCourseCommand(envelope.getPayload().courseId());
            reviewIntegrationUseCase.deleteReviewsByCourseId(command);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            System.err.printf("Review Service handler error: %s%n", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }

    }

    @RabbitListener(queues = "${rabbit.services.review.queues.user-deleted.name}", ackMode = "MANUAL")
    public void handleUserDeleted(
            @Payload EventEnvelope<UserDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            System.out.printf("Review Service consume: routingKey=%s messageId=%s traceId=%s%n",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");
            Long authorId = envelope.getPayload().userId();
            DeleteReviewsByUserCommand command = new DeleteReviewsByUserCommand(authorId);
            reviewIntegrationUseCase.deleteReviewsByAuthorId(command);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            System.err.printf("Review Service handler error: %s%n", e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
