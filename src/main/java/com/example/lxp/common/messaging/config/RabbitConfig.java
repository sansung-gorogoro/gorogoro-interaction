package com.example.lxp.common.messaging.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
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
    public static final String QUEUE_NAME = "poc.spring.queue";
    public static final String QUEUE_NAME2 = "poc.spring.queue2";
    public static final String DLQ_NAME = "poc.spring.queue.dlq";

    public static final String EXCHANGE_NAME = "poc.spring.exchange";
    public static final String DLX_NAME = "poc.spring.dlx";

    public static final String ROUTING_KEY = "poc.spring.routing";
    public static final String DLQ_ROUTING_KEY = "poc.spring.routing.dlq";

    /**
     * DLX(Dead Letter Exchange)와 DLQ(Dead Letter Queue)를 설정해줌으로써 컨슘시 발생한 문제를 해당 큐로 던지는 역할을한다.
     *
     * @return
     */
    @Bean
    public Queue pocQueue() {
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-queue-type", "quorum")
                //.withArgument("x-message-ttl", 60000) //필요시 (RabbitMQ Queue는 “메모리 기반”이기 때문에 무한히 쌓이면 절대 안 됨.)
                //.withArgument("x-max-length", 10000) //필요시 (RabbitMQ Queue는 “메모리 기반”이기 때문에 무한히 쌓이면 절대 안 됨.)
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    /**
     * 필요시
     */
    @Bean
    public Queue pocQueue2() {
        return QueueBuilder.durable(QUEUE_NAME2)
                .withArgument("x-queue-type", "quorum")
                .withArgument("x-dead-letter-exchange", DLX_NAME)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Queue pocDlq() {
        return QueueBuilder.durable(DLQ_NAME)
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
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setConcurrentConsumers(3); //기본으로 비동기적으로 동작하는 컨슈머의 수
        factory.setMaxConcurrentConsumers(10); //최대 비동기 컨슈머의 수
        factory.setPrefetchCount(10); //한 번에 얼마나 많은 메시지를 미리 가져올(버퍼링할)지
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    /**
     * @param pocExchange: 어떤 Exchange로 들어온 메시지를
     * @param pocQueue:    어떤 Queue로 라우팅할지 연결하는 규칙
     *                     <p>
     *                     프로듀서가 메세지를 보낼 때 Queue 이름을 직접 입력하지 않음 (확장성)
     *                     exchange + routing key의 형태로 지정하기 때문에
     *                     메시지 → Exchange로 들어오고
     *                     Exchange가 RoutingKey에 따라 메시지를 Queue로 전달
     *                     <p>
     *                     즉, 래빗 MQ에는 Exchange + Routing Key 로 구성된 하나의 메세지 통로가 있을 때
     *                     Exchange + Routing Key의 메세지를 듣는 여러개의 큐가 있음(브로드 캐스팅)
     *                     Exchange + Routing Key - queue1 -> 서버1
     *                     ㄴ queue2 -> 서버2
     *                     따라서, 해당 설정을 통해 컨슈머가 어떤 큐로 메세지를 받을지 래빗MQ 브로커에게 전달
     */
    @Bean
    public Binding pocQueueBinding(Queue pocQueue, TopicExchange pocExchange) {
        return BindingBuilder
                .bind(pocQueue)
                .to(pocExchange)
                .with(ROUTING_KEY);
    }

    @Bean
    public Binding pocQueue2Binding(Queue pocQueue2, TopicExchange pocExchange) {
        return BindingBuilder
                .bind(pocQueue2)
                .to(pocExchange)
                .with(ROUTING_KEY);
    }

    /**
     * DLQ의 경우 dead letter queue라는 뜻인데,
     * 죽은 페이로드를 어디에 보낼지 설정하는 것
     * handle 함수에서 제대로 처리 되지 않을 경우 (catch문을 탈경우)
     * 해당 큐로 다시 처리 된다.
     *
     * @param pocDlq
     * @param pocDlx
     * @return
     */
    @Bean
    public Binding pocDlqBinding(Queue pocDlq, TopicExchange pocDlx) {
        return BindingBuilder
                .bind(pocDlq)
                .to(pocDlx)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public TopicExchange pocExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public TopicExchange pocDlx() {
        return new TopicExchange(DLX_NAME);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

