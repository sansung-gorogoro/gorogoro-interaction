package com.example.lxp.common.messaging.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    /**
     * 레디스는 멱등성을 기본으로 한다. 이미 큐가 존재할 경우 생성하지 않는다.
     * DLQ는 아마 미리 만들어 둘듯?
     * <p>
     * 메시지가 DLX로 가는 대표적인 경우:
     * 1. Consumer가 basicNack(..., requeue=false) 또는 basicReject(..., requeue=false)
     * 2. 메시지 TTL 만료
     * 3. 큐 길이 초과(x-max-length 초과)
     */
    private final MessagingProps props;

    public RabbitConfig(MessagingProps props) {
        this.props = props;
    }

    /**
     * DLX(Dead Letter Exchange)와 DLQ(Dead Letter Queue)를 설정해줌으로써 컨슘시 발생한 문제를 해당 큐로 던지는 역할을한다.
     */
    @Bean
    public Queue reviewQueue() {
        return QueueBuilder.durable(props.queues().review().name())
                .withArgument("x-queue-type", "quorum")
                .withArgument("x-dead-letter-exchange", props.dlq().dlx())
                .withArgument("x-dead-letter-routing-key", props.dlq().routing())
                .build();
    }

    /**
     * 필요시
     */
    @Bean
    public Queue qnaQueue() {
        return QueueBuilder.durable(props.queues().qna().name())
                .withArgument("x-queue-type", "quorum")
                .withArgument("x-dead-letter-exchange", props.dlq().dlx())
                .withArgument("x-dead-letter-routing-key", props.dlq().routing())
                .build();
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(props.dlq().name())
                .build();
    }

    /**
     * 리스너 설정
     *
     * @RabbitListener을 사용할 경우, SimpleRabbitListenerContainer는 자동으로 생성된다.
     * SimpleRabbitListenerContainerFactory는 Container를 설정하는 주체
     * 따라서 팩토리를 설정해두면 자동으로 만들어지는 컨테이너에 설정이 입혀지는 식
     * container자체를 설정할 수 있지만, 사용성이 별로 안좋음
     * MessageListener를 따로 만들어줘야 하는데, 굳이?
     * <p>
     * rabbitListenerContainerFactory로 이름을 꼭 붙여줘야함 다른 이름일 경우
     * 리스너가 등록될때 factory를 못찾고 기본 팩토리로 박아버림
     * 다른 이름으로 할 경우 @RabbitListener(containerFactory = myFactory)처럼 설정 필요
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf,
            MessageConverter jsonMessageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setConcurrentConsumers(3); //기본으로 비동기적으로 동작하는 컨슈머의 수
        factory.setMaxConcurrentConsumers(10); //최대 비동기 컨슈머의 수
        factory.setPrefetchCount(10); //한 번에 얼마나 많은 메시지를 미리 가져올(버퍼링할)지
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jsonMessageConverter);
        return factory;
    }

    @Bean
    public Declarables reviewBindings(Queue reviewQueue, TopicExchange eventExchange) {
        return new Declarables(
                props.queues().review().bindings().stream()
                        .map(rk -> BindingBuilder.bind(reviewQueue).to(eventExchange).with(rk))
                        .toList()
        );
    }

    @Bean
    public Declarables qnaBindings(Queue qnaQueue, TopicExchange eventExchange) {
        return new Declarables(
                props.queues().qna().bindings().stream()
                        .map(rk -> BindingBuilder.bind(qnaQueue).to(eventExchange).with(rk))
                        .toList()
        );
    }

    /**
     * DLQ의 경우 dead letter queue라는 뜻인데,
     * 죽은 페이로드를 어디에 보낼지 설정하는 것
     * handle 함수에서 제대로 처리 되지 않을 경우 (catch문을 탈경우)
     * 해당 큐로 다시 처리 된다.
     *
     * @param dlq
     * @param dlx
     * @return
     */
    @Bean
    public Binding dlqBinding(Queue dlq, TopicExchange dlx) {
        return BindingBuilder
                .bind(dlq)
                .to(dlx)
                .with(props.dlq().routing());
    }

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(props.exchange());
    }

    @Bean
    public TopicExchange dlx() {
        return new TopicExchange(props.dlq().dlx());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
