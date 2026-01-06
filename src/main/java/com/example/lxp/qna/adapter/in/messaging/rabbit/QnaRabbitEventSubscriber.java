package com.example.lxp.qna.adapter.in.messaging.rabbit;

import com.example.lxp.common.messaging.domain.model.EventEnvelope;
import com.example.lxp.qna.adapter.in.messaging.rabbit.dto.ChapterDeletedEvent;
import com.example.lxp.qna.adapter.in.messaging.rabbit.dto.CourseDeletedEvent;
import com.example.lxp.qna.adapter.in.messaging.rabbit.dto.LessonDeletedEvent;
import com.example.lxp.qna.adapter.in.messaging.rabbit.dto.UserDeletedEvent;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class QnaRabbitEventSubscriber {

    private static final Logger log = LoggerFactory.getLogger(QnaRabbitEventSubscriber.class);

    // TODO: 삭제 전파 정책 확정 후 QnaIntegrationUseCase 주입 및 구현
    // private final QnaIntegrationUseCase qnaIntegrationUseCase;

    @RabbitListener(queues = "${rabbit.services.qna.queues.course-deleted.name}", ackMode = "MANUAL")
    public void handleCourseDeleted(
            @Payload EventEnvelope<CourseDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("QNA Service consume: routingKey={} messageId={} traceId={}",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");

            // TODO: 삭제 전파 정책 확정 후 구현
            // Long courseId = envelope.getPayload().courseId();
            // qnaIntegrationUseCase.deleteQuestionsByCourseId(courseId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("QNA Service handler error: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "${rabbit.services.qna.queues.chapter-deleted.name}", ackMode = "MANUAL")
    public void handleChapterDeleted(
            @Payload EventEnvelope<ChapterDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("QNA Service consume: routingKey={} messageId={} traceId={}",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");

            // TODO: 삭제 전파 정책 확정 후 구현
            // List<Long> lessonIds = envelope.getPayload().lessonIds();
            // qnaIntegrationUseCase.deleteQuestionsByLessonIds(lessonIds);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("QNA Service handler error: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "${rabbit.services.qna.queues.lesson-deleted.name}", ackMode = "MANUAL")
    public void handleLessonDeleted(
            @Payload EventEnvelope<LessonDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("QNA Service consume: routingKey={} messageId={} traceId={}",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");

            // TODO: 삭제 전파 정책 확정 후 구현
            // Long lessonId = envelope.getPayload().lessonId();
            // qnaIntegrationUseCase.deleteQuestionsByLessonId(lessonId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("QNA Service handler error: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitListener(queues = "${rabbit.services.qna.queues.user-deleted.name}", ackMode = "MANUAL")
    public void handleUserDeleted(
            @Payload EventEnvelope<UserDeletedEvent> envelope,
            Message message,
            Channel channel
    ) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("QNA Service consume: routingKey={} messageId={} traceId={}",
                    message.getMessageProperties().getReceivedRoutingKey(),
                    envelope.getMetadata() != null ? envelope.getMetadata().getMessageId() : "n/a",
                    envelope.getMetadata() != null ? envelope.getMetadata().getTraceId() : "n/a");

            // TODO: 삭제 전파 정책 확정 후 구현
            // Long authorId = envelope.getPayload().userId();
            // qnaIntegrationUseCase.deleteQuestionsByAuthorId(authorId);

            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("QNA Service handler error: {}", e.getMessage(), e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
